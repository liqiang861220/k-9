package com.fsck.k9.notification;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fsck.k9.Account;
import com.fsck.k9.R;
import com.fsck.k9.activity.setup.AccountSetupIncoming;
import com.fsck.k9.activity.setup.AccountSetupOutgoing;

import static com.fsck.k9.notification.NotificationController.NOTIFICATION_LED_BLINK_FAST;
import static com.fsck.k9.notification.NotificationController.NOTIFICATION_LED_FAILURE_COLOR;


class CertificateErrorNotifications {
    private final NotificationController controller;


    public CertificateErrorNotifications(NotificationController controller) {
        this.controller = controller;
    }

    public void showCertificateErrorNotification(Account account, boolean incoming) {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, incoming);
        Context context = controller.getContext();

        Intent editServerSettingsIntent = incoming ?
                AccountSetupIncoming.intentActionEditIncomingSettings(context, account) :
                AccountSetupOutgoing.intentActionEditOutgoingSettings(context, account);

        PendingIntent editServerSettingsPendingIntent = PendingIntent.getActivity(context,
                account.getAccountNumber(), editServerSettingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title = context.getString(R.string.notification_certificate_error_title, account.getDescription());
        String text = context.getString(R.string.notification_certificate_error_text);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(getCertificateErrorNotificationIcon())
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(editServerSettingsPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        controller.configureNotification(builder, null, null,
                NOTIFICATION_LED_FAILURE_COLOR,
                NOTIFICATION_LED_BLINK_FAST, true);

        getNotificationManager().notify(notificationId, builder.build());
    }

    public void clearCertificateErrorNotifications(Account account, boolean incoming) {
        int notificationId = NotificationIds.getCertificateErrorNotificationId(account, incoming);
        getNotificationManager().cancel(notificationId);
    }

    private int getCertificateErrorNotificationIcon() {
        //TODO: Use a different icon for certificate error notifications
        return controller.platformSupportsVectorDrawables() ?
                R.drawable.ic_notify_new_mail_vector : R.drawable.ic_notify_new_mail;
    }

    private NotificationManagerCompat getNotificationManager() {
        return controller.getNotificationManager();
    }
}
