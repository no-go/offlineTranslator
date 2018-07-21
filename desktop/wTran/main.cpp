#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQuickWindow>
#include "myclass.h"

int main(int argc, char *argv[]) {
    QCoreApplication::setAttribute(Qt::AA_EnableHighDpiScaling);

    QGuiApplication app(argc, argv);
    app.setWindowIcon(QIcon(":/img/logo.ico"));
    qmlRegisterType<MyClass>("wtran.myclass", 1, 0, "MyClass");

    QQmlApplicationEngine engine;
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));
    if (engine.rootObjects().isEmpty()) return -1;
    QList<QObject*> xs = engine.rootObjects();
    QQuickWindow *window = qobject_cast<QQuickWindow*>(xs.at(0));
    MyClass myClass;

    QObject::connect(
        window,
        SIGNAL(qmlSearch(QString)),
        &myClass,
        SLOT(cppSearch(QString))
    );
    //QListView *lv = window->findChild<QListView*>("listView"); // ->insertRow(model->rowCount());

    QFile file(":/raw/worte.txt");
    if(!file.open(QIODevice::ReadOnly)) {
        qDebug() << file.errorString();
    }
    QTextStream in(&file);
    while(!in.atEnd()) {
        QString line = in.readLine();
        QStringList fields = line.split(";");
        myClass.dataWorte[fields.at(0)] = line.replace(";"," - ");
        //qDebug() << '.';
    }
    file.close();

    return app.exec();
}
