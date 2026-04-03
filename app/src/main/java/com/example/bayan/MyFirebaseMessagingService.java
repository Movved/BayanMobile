package com.example.bayan;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Si le message contient une notification
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            // Appelle ici ta méthode pour afficher la notif sur l'écran
            showNotification(title, message);
        }
    }

    private void showNotification(String title, String messageBody) {
        //1. Définir ce qui se passe quand on clique sur la notification
        //Ici, on ouvre la MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Le PendingIntent est une "autorisation" donnée au système d'ouvrir l'app pour nous
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        //2. Créer le canal de notification (Obligatoire pour Android 8.0+)
        String channelId = "bayan_news_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Alertes Scolaires Bayan",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications pour les nouvelles annonces de l'école");
            notificationManager.createNotificationChannel(channel);
        }

        //3. Construire la notification visuelle
        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.bayan1_round) //l'icône de l'app
                        .setContentTitle(title)              //Le titre
                        .setContentText(messageBody)         //Le message
                        .setAutoCancel(true)                 //Disparaît quand on clique dessus
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        //4. Afficher la notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}