package com.application;

import static util.CommonUtilities.SENDER_ID;
import static util.CommonUtilities.displayMessage;
import util.ServerUtilities;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.drapp.R;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     *M�thode appel�e sur le p�riph�rique enregistr�
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Votre appareil est enregistrer avec GCM");
        
        ServerUtilities.register(context,  MainActivity.email, registrationId);
    }

    /**
     * M�thode appel�e sur le p�riph�rique non enregistr�
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * M�thode appel�e sur r�ception d'un nouveau message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
        
        displayMessage(context, message);
        
        generateNotification(context, message);
    }

    /**
     * M�thode appel�e lors de la r�ception d'un message supprim�
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Recevoir des notifications de messages supprim�s");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        
        generateNotification(context, message);
    }

    /**
     * M�thode appel�e en cas d'erreur
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "erreur Re�u : " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
       
        Log.i(TAG, "erreur Re�u r�cup�rable: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * �met une notification pour informer l'utilisateur que le serveur a envoy� un message
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // d�finir l'intention de sorte qu'il ne d�marre pas une nouvelle activit�
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // son de notification
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        // Vibrer si vibration est activ�e
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
