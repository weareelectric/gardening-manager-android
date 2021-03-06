package org.gots.garden.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.TokenRequestInterceptor;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NuxeoGardenProvider extends LocalGardenProvider {

    private static final String TAG = "NuxeoGardenProvider";

    String myToken = GotsPreferences.getInstance(mContext).getToken();

    String myLogin = GotsPreferences.getInstance(mContext).getNUXEO_LOGIN();

    String myDeviceId = GotsPreferences.getInstance(mContext).getDeviceId();

    private long TIMEOUT = 10;

    protected String myApp = GotsPreferences.getInstance(mContext).getGardeningManagerAppname();

    public NuxeoGardenProvider(Context context) {
        super(context);
    }

    @Override
    public GardenInterface createGarden(GardenInterface garden) {
        super.createGarden(garden);
        return createRemoteGarden(garden);
    }

    protected GardenInterface createRemoteGarden(GardenInterface garden) {
        try {
            AsyncTask<GardenInterface, Integer, Document> task = new AsyncTask<GardenInterface, Integer, Document>() {

                private Document newGarden;

                @Override
                protected Document doInBackground(GardenInterface... params) {
                    GardenInterface currentGarden = params[0];
                    HttpAutomationClient client = new HttpAutomationClient(
                            GotsPreferences.getGardeningManagerServerURI());
                    client.setRequestInterceptor(new TokenRequestInterceptor(
                            myApp, myToken, myLogin, myDeviceId));
                    Session session = client.getSession();

                    try {
                        DocRef wsRef = new DocRef(
                                "/default-domain/UserWorkspaces/"
                                        + GotsPreferences.getInstance(mContext).getNUXEO_LOGIN());

                        newGarden = (Document) session.newRequest(
                                "Document.Create").setInput(wsRef).set("type",
                                "Garden").set("name",
                                currentGarden.getLocality())

                        .set("properties",
                                "dc:title=" + currentGarden.getLocality()).execute();

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return newGarden;
                }

            }.execute(garden);
            // TODO wait for task.getStatus() == Status.FINISHED; in a thread
            //
            // TODO send as intent
            // TODO get(timeout)
            Document remoteGarden = task.get();
            garden.setUUID(remoteGarden.getId());

            super.updateGarden(garden);

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return garden;
    }

    @Override
    public GardenInterface getCurrentGarden() {
        return super.getCurrentGarden();
    }

    @Override
    public List<GardenInterface> getMyGardens() {

        List<GardenInterface> myGardens = super.getMyGardens();

        // TODO Sync with remote
        try {
            AsyncTask<Void, Integer, List<GardenInterface>> task = new AsyncTask<Void, Integer, List<GardenInterface>>() {

                @Override
                protected List<GardenInterface> doInBackground(Void... none) {
                    Log.d(TAG, "doInBackground");
                    HttpAutomationClient client = new HttpAutomationClient(
                            GotsPreferences.getGardeningManagerServerURI());
                    Log.d(TAG, GotsPreferences.getGardeningManagerServerURI());

                    client.setRequestInterceptor(new TokenRequestInterceptor(
                            myApp, myToken, myLogin, myDeviceId));
                    Log.d(TAG, "Token=" + myToken);

                    // Session session =
                    // client.getSession(GotsPreferences.getInstance(mContext).getNUXEO_LOGIN(),
                    // GotsPreferences.getInstance(mContext).getNUXEO_PASSWORD());
                    Session session = client.getSession();
                    Log.d(TAG, "Session=" + session);

                    // DocumentService rs = new DocumentService(session);
                    List<GardenInterface> remoteGardens = new ArrayList<GardenInterface>();
                    try {
                        // DocRef wsRef = new
                        // DocRef("/default-domain/UserWorkspaces/" +
                        // GotsPreferences.getUsername());

                        // Documents gardensWorkspaces = (Documents)
                        // rs.getChildren(wsRef);
                        Documents gardensWorkspaces = (Documents) session.newRequest(
                                "Document.Query").setHeader(
                                Constants.HEADER_NX_SCHEMAS, "*").set(
                                "query",
                                "SELECT * FROM Garden WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC").execute();
                        for (Iterator<Document> iterator = gardensWorkspaces.iterator(); iterator.hasNext();) {

                            Document gardenWorkspace = iterator.next();
                            // Document gardenWorkspace = iterator.next();
                            Log.d(TAG, "Document=" + gardenWorkspace.getId());

                            GardenInterface garden = NuxeoGardenConvertor.convert(gardenWorkspace);
                            //
                            // GardenDBHelper helperGarden = new
                            // GardenDBHelper(mContext);
                            // helperGarden.insertGarden(garden);

                            remoteGardens.add(garden);

                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    return remoteGardens;
                }

            }.execute();
            // TODO wait for task.getStatus() == Status.FINISHED; in a thread
            List<GardenInterface> remoteGardens = task.get();

            // TODO send as intent
            for (GardenInterface remoteGarden : remoteGardens) {
                boolean found = false;
                for (GardenInterface localGarden : myGardens) {
                    if (remoteGarden.getUUID() != null
                            && remoteGarden.getUUID().equals(
                                    localGarden.getUUID())) {
                        // local and remote
                        // 1: overwrite remote
                        updateRemoteGarden(localGarden);
                        // 2: TODO sync with remote instead
                        // syncGardens(localGarden,remoteGarden);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // remote only
                    myGardens.add(super.createGarden(remoteGarden));
                }
            }
            int i = 0;
            for (GardenInterface localGarden : myGardens) {

                if (localGarden.getUUID() == null) {
                    // local only
                    createRemoteGarden(localGarden);
                } else {
                    boolean found = false;
                    for (GardenInterface remoteGarden : remoteGardens) {
                        if (remoteGarden.getUUID() != null
                                && remoteGarden.getUUID().equals(
                                        localGarden.getUUID())) {
                            // local and remote
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // local only with UUID -> delete local
                        super.removeGarden(localGarden);
                        myGardens.remove(i);
                    }
                }

                i++;
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return myGardens;
    }

    @Override
    public int removeGarden(GardenInterface garden) {
        AsyncTask<GardenInterface, Integer, List<GardenInterface>> task = new AsyncTask<GardenInterface, Integer, List<GardenInterface>>() {

            @Override
            protected List<GardenInterface> doInBackground(
                    GardenInterface... gardens) {
                Log.d(TAG, "doInBackground");
                HttpAutomationClient client = new HttpAutomationClient(
                        GotsPreferences.getGardeningManagerServerURI());
                Log.d(TAG, GotsPreferences.getGardeningManagerServerURI());

                client.setRequestInterceptor(new TokenRequestInterceptor(myApp,
                        myToken, myLogin, myDeviceId));
                Log.d(TAG, "Token=" + myToken);

                // Session session =
                // client.getSession(GotsPreferences.getInstance(mContext).getNUXEO_LOGIN(),
                // GotsPreferences.getInstance(mContext).getNUXEO_PASSWORD());
                Session session = client.getSession();
                Log.d(TAG, "Session=" + session);

                // DocumentService rs = new DocumentService(session);
                List<GardenInterface> remoteGardens = new ArrayList<GardenInterface>();
                try {

                    // DocumentManager documentManager =
                    // session.getAdapter(DocumentManager.class);
                    // documentManager.remove(gardens[0].getUUID());
                    Documents gardensWorkspaces = (Documents) session.newRequest(
                            DocumentManager.DeleteDocument).setHeader(
                            Constants.HEADER_NX_SCHEMAS, "*").setInput(
                            new IdRef(gardens[0].getUUID())).execute();

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return remoteGardens;
            }

        }.execute();
        // TODO wait for task.getStatus() == Status.FINISHED; in a thread
        // List<GardenInterface> remoteGardens = task.get();
        return 0;
    }

    @Override
    /**
     * Remote update
     */
    public GardenInterface updateGarden(GardenInterface garden) {
        super.updateGarden(garden);
        updateRemoteGarden(garden);
        return garden;
    }

    protected void updateRemoteGarden(GardenInterface garden) {
        // http://doc.nuxeo.com/display/NXDOC/Operations+Index#OperationsIndex-Save
        // TODO Document.Fetch + Document.Save
        // AsyncTask<GardenInterface, Integer, Document> task = new
        // AsyncTask<GardenInterface, Integer, Document>() {
        //
        // private Document newGarden;
        //
        // @Override
        // protected Document doInBackground(GardenInterface... params) {
        // GardenInterface currentGarden = params[0];
        // HttpAutomationClient client = new
        // HttpAutomationClient(GotsPreferences.getGardeningManagerServerURI());
        //
        // String myToken = GotsPreferences.getInstance(mContext).getToken();
        // client.setRequestInterceptor(new TokenRequestInterceptor(myToken));
        //
        // Session session = client.getSession();
        //
        // try {
        // // DocRef wsRef = new DocRef("/default-domain/UserWorkspaces/"
        // // + GotsPreferences.getInstance(mContext).getNUXEO_LOGIN());
        // currentGarden.getUUID();
        //
        // newGarden = (Document)
        // session.newRequest("Document.Modify").setInput(wsRef).set("type",
        // "Garden")
        // .set("name", currentGarden.getLocality())
        //
        // .set("properties", "dc:title=" +
        // currentGarden.getLocality()).execute();
        //
        // } catch (Exception e) {
        // Log.e(TAG, e.getMessage(), e);
        // }
        // return newGarden;
        // }
        //
        // }.execute(garden);
    }
}
