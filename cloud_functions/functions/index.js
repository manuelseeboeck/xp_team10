// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
const nodemailer = require('nodemailer');


// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

const APP_NAME = 'L|I|T';

exports.sendFriendNotification = functions.database.ref('/users/{user_id}/friends/{friend}').onCreate((snapshot, context) => {
  const friend = snapshot.val();
  if (friend === "Pending") {
    const user = snapshot.ref.parent.parent.key;
    
    console.log("Friend Request");
    console.log("User_ID:" + user);
    const sender = snapshot.ref.key;
    console.log("Sender_ID:" + sender);

    // admin.database().ref('/users/').child(user).child('eMail')
    
     admin.database().ref('/users/' + user).once('value').then(function(snapshot) {
        var emailNotifications = (snapshot.val() && snapshot.val().emailNotifications);
        console.log("emailNotifications:" + emailNotifications);
        if (emailNotifications) {
          var email = (snapshot.val() && snapshot.val().eMail);
          var firstName = (snapshot.val() && snapshot.val().firstName);

          console.log("Email:" + email);

          admin.database().ref('/users/' + sender).once('value').then(function(snapshot) {
            var sender_email = (snapshot.val() && snapshot.val().eMail);
            var sender_firstName = (snapshot.val() && snapshot.val().firstName);
            var sender_lastName = (snapshot.val() && snapshot.val().lastName);
            var sender_name = sender_firstName + " " + sender_lastName;
            console.log("Sender_Email:" + sender_email);

            sendFriendRequestEmail(sender_email, email, firstName, sender_name);

          });
        }

     });
    // return sendWelcomeEmail(email, "Friend Test");
  }
});

// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
  // Grab the text parameter.
  const original = req.query.text;
  // Push the new message into the Realtime Database using the Firebase Admin SDK.
  return admin.database().ref('/messages').push({original: original}).then((snapshot) => {
    // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
    return res.redirect(303, snapshot.ref.toString());
  });
});

exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
// [END onCreateTrigger]
  // [START eventAttributes]
  const email = user.email; // The email of the user.
  const displayName = user.displayName; // The display name of the user.
  // [END eventAttributes]

  return sendWelcomeEmail(email, displayName);
});

function sendWelcomeEmail(email, displayName) {
  const mailOptions = {
    from: `${APP_NAME} <noreply.xp10@gmail.com>`,
    to: email,
  };

  // The user subscribed to the newsletter.
  mailOptions.subject = `Welcome to ${APP_NAME}!`;
  mailOptions.text = `Hey ${displayName || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
  return mailTransport.sendMail(mailOptions).then(() => {
    return console.log('New welcome email sent to:', email);
  });
}

function sendFriendRequestEmail(sender, receiver, receiverFirstName, senderName) {
    const mailOptions = {
    from: `${APP_NAME} <noreply.xp10@gmail.com>`,
    to: receiver,
  };

  mailOptions.subject = `${APP_NAME} Friend Request`;
  mailOptions.text = `Hey ${receiverFirstName || ''}! ${senderName}, has send you a friend request on ${APP_NAME}!`;
  return mailTransport.sendMail(mailOptions).then(() => {
    return console.log('New friend request email sent to:', receiver);
  });
}
