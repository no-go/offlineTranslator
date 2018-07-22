import QtQuick 2.9
import QtQuick.Controls 2.2
import wtran.myclass 1.0

Rectangle {
    id: applicationWindow
    visible: true
    width: 320
    height: 480
    color: "#35459c"
    property alias button1: button1

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
        anchors.bottom: row.top
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
            model: myModel
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

/*
        ListModel {
            id: model
            ListElement { title: "Germany"; body: "asa" }
            ListElement { title: "France"; body: "asa" }
            ListElement { title: "Italy"; body: "asa" }
            ListElement { title: "United Kingdom"; body: "asa" }
        }
*/
    }

    Grid {
        id: row
        y: 0
        anchors.horizontalCenter: parent.horizontalCenter
        anchors.bottom: parent.bottom
        anchors.bottomMargin: 0
        spacing: 2
        columns: 4

        Rectangle {
            width: 60
            height: 40
            Button {
                id: button1
                text: qsTr("Button")
                anchors.fill: parent
                display: AbstractButton.IconOnly
                icon.source: "img/worte.png"
            }
        }

        Rectangle {
            width: 60
            height: 40
            Button {
                id: button2
                text: qsTr("Button")
                anchors.fill: parent
                display: AbstractButton.IconOnly
                icon.source: "img/deu_eng.png"
            }
        }

        Rectangle {
            width: 60
            height: 40
            Button {
                id: button3
                text: qsTr("Button")
                anchors.fill: parent
                display: AbstractButton.IconOnly
                icon.source: "img/ara_eng.png"
            }
        }

        Rectangle {
            width: 60
            height: 40
            Button {
                id: button4
                text: qsTr("Button")
                anchors.fill: parent
                display: AbstractButton.IconOnly
                icon.source: "img/kur_deu.png"
            }
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
