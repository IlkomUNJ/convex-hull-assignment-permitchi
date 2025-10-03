#ifndef DRAWINGWIDGET_H
#define DRAWINGWIDGET_H

#include <QWidget>
#include <QVector>
#include <QPointF>

class QPaintEvent;
class QMouseEvent;

class DrawingWidget : public QWidget
{
    Q_OBJECT
public:
    explicit DrawingWidget(QWidget *parent = nullptr);

public slots:
    void runBothAlgorithms();
    void clearAll();

protected:
    void paintEvent(QPaintEvent *event) override;
    void mousePressEvent(QMouseEvent *event) override;

private:
    QVector<QPointF> points;
    QVector<int> hullFast;
    QVector<int> hullSlow;
    qint64 iterationsFast;
    qint64 iterationsSlow;

    void computeMonotoneChain(qint64 &iterasi, QVector<int> &hasil);
    void computeSlowConvexHull(qint64 &iterasi, QVector<int> &hasil);

    static double cross(const QPointF &o, const QPointF &a, const QPointF &b);
    static double crossVec(const QPointF &a, const QPointF &b);
    static double dist2(const QPointF &a, const QPointF &b);
    void orderHullPoints(const QVector<QPointF> &pts, QVector<int> &indices);
};

#endif // MAINWINDOW_H
