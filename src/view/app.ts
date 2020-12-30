interface SendMessage {
    type: string,
    sender: string,
    recipient: string,
    text: string
}

// @ts-ignore
var app = new Vue({
    el: '#message',
    data: {
        utter: '',
        messages: [{
            "who": "fucker",
            "what": "lover!"
        }, {
            "who": "lover",
            "what": "fucker!"
        }],
        me: '',
        recipient: ''
    },
    methods: {
        send: function () {
            ws.send(JSON.stringify({
                "type": "send",
                "sender": this.me,
                "recipient": this.recipient,
                "text": this.utter
            }));
            this.messages.push({
                "who": this.me,
                "what": this.utter
            });
        }
    }
})

var ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");

ws.onopen = function (evt) {
    console.log("Connection open ...");
    app.me = prompt("you id")
    ws.send(JSON.stringify({
        "type": "listen",
        "id": app.me
    }));
};

ws.onmessage = function (evt) {
    console.log("Received Message: " + evt.data);
    var message: SendMessage = JSON.parse(evt.data)
    app.messages.push({
        "who": message.sender,
        "what": message.text
    });
};

ws.onclose = function (evt) {
    console.log("Connection closed.");
};