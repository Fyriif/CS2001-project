const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

function dayOfWeekAsString(dayIndex) {
    return ["sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"][dayIndex];
}

exports.sendWorkOutNotification = functions
    .region('europe-west1')
    .https.onRequest((req, res) => {
        var now = new Date();

        admin.firestore().collection('notification-tokens').get().then(snapshot => {
            var users = [];
            snapshot.forEach(doc => {
                var user = {
                    uid: doc.id,
                    token: doc.data().token,
                    timeZone: doc.data().time_zone,
                };
                users.push(user);
            });

            users.forEach(function(entry) {
                var docRef = admin.firestore().collection('user-info').doc(entry.uid);
                docRef.get().then(function(doc) {
                    if (doc.exists) {
                        const workOutDays = doc.data().work_out_days; 
                        const userTime = new Date(Date.parse(new Date().toLocaleString("en-US", {timeZone: entry.timeZone})));
                        const times = workOutDays[dayOfWeekAsString(userTime.getDay()).toLowerCase()];

                        if (times != null && times.length > 0) {
                            var startTimeHour = times[0].split(":")[0].trim();
                            var startTimeMin = times[0].split(":")[1].trim();

                            var endTimeHour = times[1].split(":")[0].trim();
                            var endTimeMin = times[1].split(":")[1].trim();

                            if (startTimeHour == userTime.getHours() && startTimeMin == userTime.getMinutes()) {
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
                        console.log("User (" + entry.uid + ") has no user-info document!");
                    }
                }).catch(function(error) {
                    console.log("Error getting document:", error);
                })
            });

            res.send(users)
            return "Sent notification!";
        }).catch(reason => {
            res.send(reason)
        })
    });