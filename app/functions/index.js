const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

var weekday = new Array(7);
weekday[0] =  "Sunday";
weekday[1] = "Monday";
weekday[2] = "Tuesday";
weekday[3] = "Wednesday";
weekday[4] = "Thursday";
weekday[5] = "Friday";
weekday[6] = "Saturday";

exports.sendWorkOutNotification = functions
    .region('europe-west1')
    .https.onRequest((req, res) => {
        admin.firestore().collection('notification-tokens').get().then(snapshot => {
            var userUids = [];
            snapshot.forEach(doc => {
                var user = {
                    uid: doc.id,
                    token: doc.data().token,
                };
                userUids.push(user);
            });

            userUids.forEach(function(entry) {
                var docRef = admin.firestore().collection('user-info').doc(entry.uid);
                docRef.get().then(function(doc) {
                    if (doc.exists) {
                        const workOutDays = doc.data().workOutDays;

                        var d = new Date();
                        var times = workOutDays[weekday[d.getDay()].toLowerCase()];

                        // We have workout times
                        if (times.length > 0) {
                            var startTimeHour = times[0].split(":")[0].trim();
                            var startTimeMin = times[0].split(":")[1].trim();

                            var endTimeHour = times[1].split(":")[0].trim();
                            var endTimeMin = times[1].split(":")[1].trim();

                            if (startTimeHour == d.getHours() && startTimeMin == d.getMinutes()) {
                                const payload = {
                                    notification: {
                                        title: 'BFit: Workout Time!',
                                        body: 'Its that time again, let us begin...'
                                    }
                               };

                               console.log("Sent notification: ", entry.uid, entry.token, payload)
                               admin.messaging().sendToDevice(entry.token, payload)
                            }
                        }
                    } else {
                        console.log("No such document!");
                    }
                }).catch(function(error) {
                    console.log("Error getting document:", error);
                })
            });

            res.send(userUids)
            return "";
        }).catch(reason => {
            res.send(reason)
        })
    });