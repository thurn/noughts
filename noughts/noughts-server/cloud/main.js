var Mandrill = require('mandrill');
var _ = require('underscore');
Mandrill.initialize('IkwOpINs9HwrjuE4FUAffw');

Parse.Cloud.define("emailInvite", function(request, response) {
  var message = request.params.message;
  var email = request.params.email;
  var gameId = request.params.gameId;
  message = _.escape(message).replace(/\n/g, "<br />")

  if (message != null && message.length > 0 && email != null &&
      email.length > 0 && gameId != null && gameId.length > 0) {
    var body =
        "<p><img src='http://noughts.firebaseapp.com/assets/logo250.png' width='250px'/></p>" +
        "<p><%= message %></p>" +
        "<p><a href='http://noughts.firebaseapp.com/open?id=<%- gameId %>'>http://noughts.firebaseapp.com/open?id=<%- gameId %></a></p>"
    Mandrill.sendEmail({
      message: {
        html: _.template(body, {message: message, gameId: gameId}),
        subject: "Invitation to play noughts",
        from_email: "noreply@noughts.firebaseapp.com",
        from_name: "noughts",
        to: [
          {
            email: email,
          }
        ]
      },
      async: true
    },{
      success: function(httpResponse) {
        console.log(httpResponse);
        response.success("Email sent!");
      },
      error: function(httpResponse) {
        console.error(httpResponse);
        response.error("Uh oh, something went wrong");
      }
    });
  } else {
    response.error("Invalid request");
  }
});
