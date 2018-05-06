package fruchtermanReingold;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import fruchtermanReingold.options.SimpleOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FruchtermanReingoldLayoutProvider extends AbstractLayoutProvider {

    public double Width;
    public double Height;
    public final int Iterations = 100;
    public double k;
    public double[][] displacement;
    public int temperature = 100;

    @Override
    public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {

        layoutGraph.setWidth(800);
        layoutGraph.setHeight(800);
        
        Width = layoutGraph.getWidth();
        Height = layoutGraph.getHeight();
        // Get and possibly reverse the list of nodes to lay out
        List<ElkNode> nodes = new ArrayList<>(layoutGraph.getChildren());

        for (ElkNode node : nodes) {
         node.setX(Math.random()*Width);
         node.setY(Math.random()*Height);
        }
        displacement = new double[nodes.size()][2];
        k = Math.sqrt((Width * Height) / nodes.size());
        for (int i = 0; i < Iterations; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                ElkNode node = nodes.get(j);
                for (int l = 0; l < nodes.size(); l++) {
                    node.setIdentifier(Integer.toString(j));
                    ElkNode node2 = nodes.get(l);
                    if (!node.equals(node2)) {
                        double xDiff = node.getX() - node2.getX();
                        double yDiff = node.getY() - node2.getY();
                        // to do node 1 koennte auf node 2 liegen
                        displacement[j][0] +=
                                (xDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * repellingForce(xDiff);
                        displacement[j][1] +=
                                (yDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * repellingForce(yDiff);
                    }

                }

            }
            for (ElkEdge edge : layoutGraph.getContainedEdges()) {
                ElkNode source = ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));
                ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));
                double xDiff = source.getX() - target.getX();
                double yDiff = source.getY() - target.getY();

                displacement[Integer.parseInt(source.getIdentifier())][0] += 
                        (xDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * attractiveForce(xDiff);
                displacement[Integer.parseInt(source.getIdentifier())][1] += 
                        (yDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * attractiveForce(yDiff);
                displacement[Integer.parseInt(target.getIdentifier())][0] += 
                        (xDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * attractiveForce(xDiff);
                displacement[Integer.parseInt(target.getIdentifier())][1] += 
                        (yDiff / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff))) * attractiveForce(yDiff);
            }
            for (ElkNode node : nodes) {
               double xDisp = displacement[Integer.parseInt(node.getIdentifier())][0];
               double yDisp = displacement[Integer.parseInt(node.getIdentifier())][1]; 
                node.setX((node.getX()+xDisp)/Math.sqrt((xDisp*xDisp)+(yDisp*yDisp))*
                        Math.min(temperature, Math.sqrt((xDisp*xDisp)+(yDisp*yDisp))));
                node.setY((node.getY()+yDisp)/Math.sqrt((xDisp*xDisp)+(yDisp*yDisp))*
                        Math.min(temperature, Math.sqrt((xDisp*xDisp)+(yDisp*yDisp))));
                node.setX(Math.min(Width/2, Math.max(Width/(-2), node.getX())));
                node.setX(Math.min(Height/2, Math.max(Height/(-2), node.getY())));
            }
            temperature = cool(temperature);
        }
    }

    private double attractiveForce(double distance) {
        return ((distance * distance) / k);
    }

    private double repellingForce(double distance) {
        return ((k * k) / distance);
    }
    
    private int cool (int t) {
        return t-1;
    }
}
