'use strict';

// [START gae_flex_quickstart]
const express = require('express');

const app = express();

app.get('/', (req, res) => {
  res.status(200).send('Hello, world!').end();
});

// Start the server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});

var admin = require('firebase-admin');
var request = require('request');

var API_KEY = "AAAAxukWRF0:APA91bG5Ow5zHbNFCVhGnGAz1kZWRh-ARbfegWs5it4MmLv7JKEx6kAU8_3BAxHtzO5DBKYDtW2oi2z08gxAFeLFp2dodu0ZWvDQXreia0frzKl1v0DK9ND34XUcdosAD2wo3M5iybzF"; // Your Firebase Butt Messaging Server API key

// Fetch the service account key JSON file contents
var serviceAccount = require("./the-banker-65f48-firebase-adminsdk-350tx-4000cee6fb.json");

// Initialize the app with a service account, granting admin privileges
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://the-banker-65f48.firebaseio.com/"
});
var ref = admin.database().ref();

function listenForNotificationRequests() {
    var requests = ref.child('notificationRequests');
    requests.on('child_added', function(requestSnapshot) {
        var request = requestSnapshot.val();

        function onNotificationSuccess() {
            requestSnapshot.ref.remove();
        }

        if (request.NOTIFICATION_TYPE == 'START_GAME_NOTIFICATION') {
            sendStartGameNotification(
                request.gameId,
                onNotificationSuccess);
        }

        else {
            console.log('Failed to recognize notification type: ' + request.NOTIFICATION_TYPE);
        }
    },
    function(error) {
        console.error(error);
    });
};

function sendStartGameNotification(gameId, onSuccess) {

    console.log('sendStartGameNotification: Trying to send START_GAME_NOTIFICATION to ' + gameId);

    request({
        url: 'https://fcm.googleapis.com/fcm/send',
        method: 'POST',
        headers: {
            'Content-Type' :' application/json',
            'Authorization': 'key='+API_KEY
        },

        body: JSON.stringify({

            data: {
                "GAME_ID" : gameId
            },
            notification: {
                body: 'START_GAME_NOTIFICATION',
            },
            to : '/topics/' + gameId
        })
        },

        function(error, response, body) {
            if (error) { console.error(error); }
                else if (response.statusCode >= 400) {
                    console.error('HTTP Error: ' + response.statusCode+' - ' + response.statusMessage);
                }
                else {
                    onSuccess();
                    console.log('sendStartGameNotification: Successfully sent START_GAME_NOTIFICATION notification');
                }
        });
}


function sendNotificationToUser(username, message, onSuccess) {
  request({
    url: 'https://fcm.googleapis.com/fcm/send',
    method: 'POST',
    headers: {
      'Content-Type' :' application/json',
      'Authorization': 'key='+API_KEY
    },
    body: JSON.stringify({
      notification: {
        body: 'this is the message body',
        title: message
      },
      to : '/topics/'+username
    })
  }, function(error, response, body) {
    if (error) { console.error(error); }
    else if (response.statusCode >= 400) {
      console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage);
    }
    else {
      onSuccess();
    }
  });
}

// start listening
listenForNotificationRequests();
