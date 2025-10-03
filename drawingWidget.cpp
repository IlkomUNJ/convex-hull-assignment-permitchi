#include "drawingwidget.h"
#include <QPainter>
#include <QMouseEvent>
#include <algorithm>
#include <set>
#include <cmath>

DrawingWidget::DrawingWidget(QWidget *parent)
    : QWidget(parent),
    iterationsFast(0),
    iterationsSlow(0)
{
    setAutoFillBackground(true);
    setBackgroundRole(QPalette::Base);
}

void DrawingWidget::mousePressEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton) {
        QPointF p = event->position();
#ifdef QT_VERSION_MAJOR
#if QT_VERSION_MAJOR < 6
        p = event->posF();
#endif
#endif
        points.append(p);
        hullFast.clear();
        hullSlow.clear();
        iterationsFast = iterationsSlow = 0;
        update();
    }
}

void DrawingWidget::paintEvent(QPaintEvent *)
{
    QPainter g(this);
    g.setRenderHint(QPainter::Antialiasing);
    g.fillRect(rect(), Qt::white);
    g.setPen(Qt::black);
    for (auto &pt : points) g.drawEllipse(pt, 4, 4);

    if (!hullSlow.isEmpty()) {
        QPen pena(Qt::red, 2);
        g.setPen(pena);
        QPolygonF poli;
        for (int i : hullSlow) poli << points[i];
        if (poli.size() > 1) {
            g.drawPolygon(poli);
            g.drawLine(poli.last(), poli.first());
        }
    }

    if (!hullFast.isEmpty()) {
        QPen pena(Qt::blue, 2, Qt::DashLine);
        g.setPen(pena);
        QPolygonF poli;
        for (int i : hullFast) poli << points[i];
        if (poli.size() > 1) {
            g.drawPolygon(poli);
            g.drawLine(poli.last(), poli.first());
        }
    }

    g.setPen(Qt::black);
    QFont f = g.font();
    f.setPointSize(10);
    g.setFont(f);

    QString info = QString("Titik: %1\nIterasi Cepat: %2\nIterasi Lambat: %3\nKlik kiri untuk tambah titik")
                       .arg(points.size())
                       .arg(iterationsFast)
                       .arg(iterationsSlow);
    g.drawText(8, 16, info);
}

void DrawingWidget::clearAll()
{
    points.clear();
    hullFast.clear();
    hullSlow.clear();
    iterationsFast = iterationsSlow = 0;
    update();
}

double DrawingWidget::cross(const QPointF &o, const QPointF &a, const QPointF &b)
{
    return (a.x() - o.x()) * (b.y() - o.y()) - (a.y() - o.y()) * (b.x() - o.x());
}

double DrawingWidget::crossVec(const QPointF &a, const QPointF &b)
{
    return a.x()*b.y() - a.y()*b.x();
}

double DrawingWidget::dist2(const QPointF &a, const QPointF &b)
{
    double dx = a.x()-b.x();
    double dy = a.y()-b.y();
    return dx*dx + dy*dy;
}

void DrawingWidget::runBothAlgorithms()
{
    hullFast.clear();
    hullSlow.clear();
    iterationsFast = iterationsSlow = 0;
    if (points.size() < 3) { update(); return; }
    computeMonotoneChain(iterationsFast, hullFast);
    computeSlowConvexHull(iterationsSlow, hullSlow);
    update();
}

void DrawingWidget::computeMonotoneChain(qint64 &iterasi, QVector<int> &hasil)
{
    iterasi = 0;
    int n = points.size();
    QVector<int> idx(n);
    for (int i=0;i<n;i++) idx[i]=i;
    std::sort(idx.begin(), idx.end(), [&](int a, int b){
        iterasi++;
        if (points[a].x() == points[b].x()) return points[a].y() < points[b].y();
        return points[a].x() < points[b].x();
    });
    QVector<int> atas, bawah;
    for (int i=0;i<n;i++) {
        while (atas.size()>=2 && cross(points[atas[atas.size()-2]], points[atas.last()], points[idx[i]]) <= 0) {
            atas.removeLast();
            iterasi++;
        }
        atas.append(idx[i]);
    }
    for (int i=n-1;i>=0;i--) {
        while (bawah.size()>=2 && cross(points[bawah[bawah.size()-2]], points[bawah.last()], points[idx[i]]) <= 0) {
            bawah.removeLast();
            iterasi++;
        }
        bawah.append(idx[i]);
    }
    if (!bawah.isEmpty()) bawah.removeFirst();
    if (!bawah.isEmpty()) bawah.removeLast();
    hasil = atas;
    for (int v : bawah) hasil.append(v);
}

void DrawingWidget::computeSlowConvexHull(qint64 &iterasi, QVector<int> &hasil)
{
    iterasi = 0;
    int n = points.size();
    if (n < 3) return;
    std::set<std::pair<int,int>> sisi;
    for (int i=0;i<n;i++) {
        for (int j=i+1;j<n;j++) {
            bool valid = true;
            bool pos=false, neg=false;
            for (int k=0;k<n;k++) {
                if (k==i||k==j) continue;
                double c = cross(points[i], points[j], points[k]);
                iterasi++;
                if (c>1e-9) pos=true;
                if (c<-1e-9) neg=true;
                if (pos&&neg) { valid=false; break; }
            }
            if (valid) {
                sisi.insert({i,j});
                sisi.insert({j,i});
            }
        }
    }
    std::set<int> simpul;
    for (auto &e : sisi) { simpul.insert(e.first); simpul.insert(e.second); }
    if (simpul.empty()) return;
    QVector<QPointF> pts;
    QVector<int> idx;
    for (int v : simpul) { pts.append(points[v]); idx.append(v); }
    orderHullPoints(pts, idx);
    hasil = idx;
}

void DrawingWidget::orderHullPoints(const QVector<QPointF> &pts, QVector<int> &idx)
{
    int m = pts.size();
    if (m<=1) return;
    double cx=0,cy=0;
    for (auto &p:pts){ cx+=p.x(); cy+=p.y(); }
    cx/=m; cy/=m;
    QPointF pusat(cx,cy);
    std::vector<std::pair<double,int>> arr;
    arr.reserve(m);
    for (int i=0;i<m;i++){
        QPointF v(pts[i].x()-pusat.x(), pts[i].y()-pusat.y());
        double ang=std::atan2(v.y(), v.x());
        arr.push_back({ang, idx[i]});
    }
    std::sort(arr.begin(), arr.end(), [](auto &a, auto &b){return a.first<b.first;});
    for (int i=0;i<m;i++) idx[i]=arr[i].second;
}

