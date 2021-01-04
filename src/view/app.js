import {ChatApp} from "./core.js";

var app = new Vue({
    el: "#app",
    data: {
        currentPage: 'welcome',
        myId: '',
        myAlisa: '',
        welcome: {
            currentAction: 'signin',
            actions: [
                {
                    key: 'signin',
                    name: '已有ID'
                },
                {
                    key: 'signup',
                    name: '创建ID'
                },
                {
                    key: 'load',
                    name: '导入ID'
                }],
            signin: {
                userId: '',
                password: '',
                accountInStorage: ChatApp.allAccountInStorage()
            },
            signup: {
                alisa: '',
                password: ''
            },
            load: {
                inf: ''
            }
        },
        message: {
            currentAddressId: '',
            messages: [],
            address: [],
            utter: '',
        },
        setting: {
            newFriend: {
                id: '',
                alisa: ''
            },
            newGroup: {
                alisa: ''
            }
        }
    },
    methods: {
        auth: function () {
            if (this.welcome.signin.userId == null || this.welcome.signin.password == null) {
                return
            }
            if (chat.login(this.welcome.signin.userId, this.welcome.signin.password)) {
                alert("Auth Pass")
                this.initMessage()
            } else {
                alert("Auth Fail")
            }
        },
        signup: function () {
            if (this.welcome.signup.alisa == null || this.welcome.signup.password == null) {
                return
            }
            ChatApp.signup(this.welcome.signup.alisa, this.welcome.signup.password)
            alert("Succeed! Please refresh")
        },
        loadAccount: function () {
            if (this.welcome.load.inf == null) {
                return
            }
            let userInf = JSON.parse(this.welcome.load.inf)
            ChatApp.loadAccount(userInf.id, userInf.inf, userInf.alisa)
            alert("Succeed! Please refresh")
        },
        initMessage: function () {
            this.currentPage = 'message'
            this.myId = chat.userId
            this.myAlisa = chat.userAlisa
            document.title = "Hello: " + this.myAlisa
            this.message.address = chat.userInf.friends
            chat.onMessage = (sender, recipient, text) => {
                this.message.messages.push({
                    sender: sender,
                    recipient: recipient,
                    text: text
                })
            }
        },
        send: function () {
            this.message.messages.push({
                sender: this.myId,
                recipient: this.message.currentAddressId,
                text: this.message.utter
            })

            chat.send(this.message.currentAddressId, this.message.utter)
        },
        id2Alisa: function (id) {
            if (id === this.myId) {
                return this.myAlisa
            }
            return this.message.address.filter(a => a.id === id)[0].alisa
        },
        exportInf: function () {
            navigator.clipboard.writeText(chat.exportAccount()).then(function () {
                alert('剪贴板复制成功');
            }, function (err) {
                alert('剪贴板复制失败，因为', err);
            });
        },
        copyId: function () {
            navigator.clipboard.writeText(this.myId).then(function () {
                alert('剪贴板复制成功');
            }, function (err) {
                alert('剪贴板复制失败，因为', err);
            });
        },
        addFriend: function () {
            chat.addFriend(this.setting.newFriend.id, this.setting.newFriend.alisa);
            // Why here it is already in this.message.address
            /*this.message.address.push({
                id: this.setting.newFriend.id,
                alisa: this.setting.newFriend.alisa
            })*/
        },
        removeFriend: function () {
            chat.removeFriend(this.message.currentAddressId)
            this.message.address = this.message.address.filter(a => a.id !== this.message.currentAddressId)
        }
    },
    computed: {
        showMessage: function () {
            return this.message.messages
                .filter(m => m.sender === this.message.currentAddressId || m.recipient === this.message.currentAddressId)
                .map(m => {
                    return {
                        sender: m.sender,
                        recipient: m.recipient,
                        text: m.text
                    }
                })
        }
    }
})

var chat = new ChatApp("ws://" + location.hostname + ":" + location.port + "/chat");