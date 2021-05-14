package rtree;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

import rtree.node.Building;
import rtree.node.DataNode;
import rtree.node.DataNodeExpectedException;
import rtree.node.IncompatibleDataTypeException;
import rtree.node.Node;
import rtree.node.Rectangle;

public class RTree {
   private Node root;

   public RTree(int maxChildrenPerNode) {
      root = Node.CreateRootNode(maxChildrenPerNode);
   }

   public RTree() {
      this(40);
   }

   public void Insert(DataNode nodeToInsert) throws Exception {
      Node leafNode = chooseLeaf(nodeToInsert, root);
      leafNode.AddChild(nodeToInsert);
   }

   public List<Building> SearchForBuildingsInArea(Rectangle searchArea, String filter)
         throws DataNodeExpectedException, IncompatibleDataTypeException {
      List<Building> foundBuildings = new LinkedList<>();
      SearchForDataNodeInArea(root, searchArea, foundBuildings,filter);
      return foundBuildings;
   }

   private void SearchForDataNodeInArea(Node nodeToTraverse, Rectangle searchArea, List<Building> foundNodes, String filter)
         throws DataNodeExpectedException, IncompatibleDataTypeException {
      List<Node> children = nodeToTraverse.GetChildren();

      if (children.size() == 0) {
         if (!(nodeToTraverse instanceof DataNode)) {
            // only data nodes have to have no children
            throw new DataNodeExpectedException();
         }

         DataNode<Building> dataNode;
         try {
            dataNode = (DataNode<Building>) nodeToTraverse;
         } catch (Exception e) {
            Class expectedGenericClass = foundNodes.getClass();
            ParameterizedType expectedParametrizedType = (ParameterizedType) expectedGenericClass
                  .getGenericSuperclass();
            Class expectedDataType = (Class) (expectedParametrizedType.getActualTypeArguments()[0]);

            Class actualGenericClass = nodeToTraverse.getClass();
            ParameterizedType actualParameterizedType = (ParameterizedType) actualGenericClass.getGenericSuperclass();
            Class actualDataType = (Class) (actualParameterizedType.getActualTypeArguments()[0]);

            // Actually we can just skip the node in case when we have data nodes with
            // different data types, it may be helpful e.g. we have `Hospital`, `Bank` data
            // types and we want to find all Hospital in area
            throw new IncompatibleDataTypeException(expectedDataType, actualDataType, e);
         }
         if(dataNode.data.getType().equals(filter)) {
            foundNodes.add(dataNode.data);
         }
         return;
      }

      for (Node childNode : children) {
         if (childNode.IsIntersects(searchArea)) {
            SearchForDataNodeInArea(childNode, searchArea, foundNodes, filter);
         }
      }
   }

   private Node chooseLeaf(Node targetNode, Node searchInNode) throws Exception {
      if (searchInNode.IsLeaf()) {
         return searchInNode;
      }

      Node minimalAreaIncreaseChildren = Node.ChooseNodeWithMinimalAreaIncrease(targetNode, searchInNode.GetChildren());
      return chooseLeaf(targetNode, minimalAreaIncreaseChildren);
   }
}
