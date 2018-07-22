#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QtWidgets/QListView>
#include <QtQuickWidgets/QtQuickWidgets>
#include <QQuickView>
#include <qqmlengine.h>
#include <qqmlcontext.h>
#include <qqml.h>
#include "myclass.h"

int main(int argc, char *argv[]) {
    QCoreApplication::setAttribute(Qt::AA_EnableHighDpiScaling);

    QGuiApplication app(argc, argv);
    app.setWindowIcon(QIcon(":/img/logo.ico"));
    qmlRegisterType<MyClass>("wtran.myclass", 1, 0, "MyClass");

    MyClass myClass;
    QList<QObject*> dataList;

    QFile file(":/raw/worte.txt");
    if(!file.open(QIODevice::ReadOnly)) {
        qDebug() << file.errorString();
    }
    QTextStream in(&file);
    while(!in.atEnd()) {
        QString line = in.readLine();
        QStringList fields = line.split(";");
        myClass.dataWorte[fields.at(0)] = line.replace(";"," - ");
    }
    file.close();

    QQuickView view;
    view.setResizeMode(QQuickView::SizeRootObjectToView);
    QQmlContext *ctxt = view.rootContext();
    ctxt->setContextProperty("myModel", QVariant::fromValue(dataList));

    view.setSource(QUrl(QStringLiteral("qrc:/main.qml")));


    QObject::connect(
        view.findChild<QObject *>("scrollView")->parent(),
        SIGNAL(qmlSearch(QString)),
        &myClass,
        SLOT(cppSearch(QString))
    );

    view.show();

    return app.exec();
}
