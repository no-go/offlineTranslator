#ifndef MYCLASS_H
#define MYCLASS_H

#include <QObject>
#include <QtDebug>
#include <QQuickWindow>
#include <QQuickItem>
#include <QString>
#include <map>
#include <string>

class MyClass : public QObject {
    Q_OBJECT
    Q_PROPERTY(QString quer READ getQuer WRITE setQuer NOTIFY onTextChanged)

private:
    QString _quer;
public:
    explicit MyClass(QObject *parent = nullptr);
    QString getQuer();
    void setQuer(const QString &quer);
    std::map<QString,QString> dataWorte;

signals:
void onTextChanged();

public slots:
    void cppSearch(QString q) {
        qDebug() << dataWorte[q];
        //setQuer(q);

        for (auto it1 = dataWorte.begin(); it1 != dataWorte.end(); ++it1) {
            QString n1 = it1->second;
            QString qq = QString("x")+q+QString("x");
            if (n1.contains(q)) {
                QString n2 = n1.replace(q,qq);
                qDebug() << n2;
            }

        }
    }
};

#endif // MYCLASS_H
