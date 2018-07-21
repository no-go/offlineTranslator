import QtQuick 2.9
import QtQuick.Controls 2.2
import wtran.myclass 1.0

ApplicationWindow {
    id: applicationWindow
    visible: true
    width: 320
    height: 480
    minimumWidth: 320
    color: "#35459c"
    title: qsTr("W Tran")

    signal qmlSearch(string x)

    MyClass {
        id: myclass
    }

    ScrollView {
        id: scrollView
        anchors.right: parent.right
        anchors.rightMargin: 0
        anchors.left: parent.left
        anchors.leftMargin: 0
        anchors.bottom: grid.top
        anchors.bottomMargin: 0
        anchors.top: textField.bottom
        anchors.topMargin: 5

        background: Rectangle {
            id: scrollBg
            radius: 0
            anchors.fill: parent
            border.width: 0
        }

        ListView {
            id: listView
            width: parent.width
            model: model
            delegate: delegate
        }

        Component {
            id: delegate
            Item {
                width: parent.width
                height: 36
                Rectangle {
                    id: trenner
                    width: parent.width
                    height: 1
                    color: "#ffe0e0e0"
                }
                Rectangle {
                    anchors.top: trenner.bottom
                    width: parent.width
                    height: parent.height
                    color: index % 2 == 0 ? "white" : "#eeffffaa"
                }
                Text {
                    id: ititle
                    color: "#335588"
                    text: title
                    font.bold: true
                    height: 16
                    anchors.left: parent.left
                    anchors.leftMargin: 5
                }
                Text {
                    id: ibody
                    anchors.top: ititle.bottom
                    anchors.left: parent.left
                    anchors.leftMargin: 5
                    height: 16
                    text: body
                }
            }
        }

        ListModel {
            id: model
            ListElement { title: "Germany"; body: "asa" }
            ListElement { title: "France"; body: "asa" }
            ListElement { title: "Italy"; body: "asa" }
            ListElement { title: "United Kingdom"; body: "asa" }
        }
    }

    Grid {
        id: grid
        y: 0
        height: 40
        clip: false
        anchors.right: parent.right
        anchors.rightMargin: 0
        anchors.left: parent.left
        anchors.leftMargin: 0
        anchors.bottom: parent.bottom
        anchors.bottomMargin: 0

        Button {
            id: button1
            text: qsTr("Button")
            anchors.bottom: parent.bottom
            anchors.bottomMargin: 0
            anchors.top: parent.top
            anchors.topMargin: 0
            anchors.right: button2.left
            anchors.rightMargin: 0
            display: AbstractButton.IconOnly
            anchors.left: parent.left
            anchors.leftMargin: 0
            icon.source: "img/worte.png"
        }

        Button {
            id: button2
            width: 70
            text: qsTr("Button")
            anchors.bottom: parent.bottom
            anchors.bottomMargin: 0
            anchors.top: parent.top
            anchors.topMargin: 0
            anchors.right: button3.left
            anchors.rightMargin: 0
            display: AbstractButton.IconOnly
            icon.source: "img/deu_eng.png"
        }

        Button {
            id: button3
            width: 70
            text: qsTr("Button")
            anchors.bottom: parent.bottom
            anchors.bottomMargin: 0
            anchors.top: parent.top
            anchors.topMargin: 0
            display: AbstractButton.IconOnly
            anchors.right: button4.left
            anchors.rightMargin: 0
            icon.source: "img/ara_eng.png"
        }

        Button {
            id: button4
            width: 70
            text: qsTr("Button")
            anchors.bottom: parent.bottom
            anchors.bottomMargin: 0
            anchors.top: parent.top
            anchors.topMargin: 0
            display: AbstractButton.IconOnly
            anchors.right: parent.right
            anchors.rightMargin: 0
            icon.source: "img/kur_deu.png"
        }
    }

    TextField {
        id: textField
        text: myclass.quer
        placeholderText: qsTr("search...")
        anchors.right: roundButton.left
        anchors.rightMargin: 5
        anchors.top: parent.top
        anchors.topMargin: 5
        anchors.left: parent.left
        anchors.leftMargin: 5

        onTextChanged: myclass.quer = text
    }

    RoundButton {
        id: roundButton
        x: 260
        text: "search"
        flat: false
        display: AbstractButton.IconOnly
        focusPolicy: Qt.StrongFocus
        anchors.top: parent.top
        anchors.topMargin: 5
        anchors.right: parent.right
        anchors.rightMargin: 5
        onClicked: applicationWindow.qmlSearch(textField.text)
        icon.source: "img/search.png"
    }



}
