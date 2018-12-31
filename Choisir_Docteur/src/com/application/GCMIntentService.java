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
     *Méthode appelée sur le périphérique enregistré
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Votre appareil est enregistrer avec GCM");
        
        ServerUtilities.register(context,  MainActivity.email, registrationId);
    }

    /**
     * Méthode appelée sur le périphérique non enregistré
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Méthode appelée sur réception d'un nouveau message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
        
        displayMessage(context, message);
        
        generateNotification(context, message);
    }

    /**
     * Méthode appelée lors de la réception d'un message supprimé
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Recevoir des notifications de messages supprimés");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        
        generateNotification(context, message);
    }

    /**
     * Méthode appelée en cas d'erreur
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "erreur Reçu : " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
       
        Log.i(TAG, "erreur Reçu récupérable: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Émet une notification pour informer l'utilisateur que le serveur a envoyé un message
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // définir l'intention de sorte qu'il ne démarre pas une nouvelle activité
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // son de notification
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        // Vibrer si vibration est activée
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
