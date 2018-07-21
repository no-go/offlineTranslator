#include <QQuickWindow>
#include "myclass.h"

MyClass::MyClass(QObject *parent) : QObject(parent) {
}

QString MyClass::getQuer() {
    return _quer;
}

void MyClass::setQuer(const QString &quer) {
    _quer = quer;
    emit onTextChanged();
}
