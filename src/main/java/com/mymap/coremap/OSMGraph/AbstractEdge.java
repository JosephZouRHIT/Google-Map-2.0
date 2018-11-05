package com.mymap.coremap.OSMGraph;

/**
 * author: Lining Pan
 */
public abstract class AbstractEdge {
    private AbstractVertex from;
    private AbstractVertex to;

    AbstractEdge(AbstractVertex f, AbstractVertex t) {
        from = f;
        to = t;
    }

    public double getCost() {
        return this.getCost(CostType.DISTANCE);
    }

    public double getCost(CostType t) {
        switch (t) {
            case TIME:
                return this.getCostByTime();
            case DISTANCE:
                return this.getCostByDistance();
            default:
                return this.getCostByTime();
        }
    }

    public AbstractVertex getFromNode() {
        return from;
    }

    public AbstractVertex getToNode() {
        return to;
    }

    public long getFromNodeID() {
        return from.getID();
    }

    public long getToNodeID() {
        return to.getID();
    }

    protected abstract double getCostByTime();

    protected abstract double getCostByDistance();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEdge other = (AbstractEdge) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            return other.to == null;
        } else return to.equals(other.to);
    }
}
