package njuzh.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TextElement;

public class ASTExtractor {

	public ASTNode node;
	public boolean hasChildren;
	public List<ASTNode> childrenNodes = new ArrayList<ASTNode>();
	//public boolean isLeaf = false; //是否是叶子节点
	public String nodeType = "unknown";
	public String sbt;
	
	public ASTExtractor(ASTNode node) {
		this.node = node;
		this.nodeType = node.getClass().toString();
		this.sbt = SBT(node);
		Object[] childObjects = getChildren(node);
		if(childObjects.length == 0) {
			this.hasChildren = false;
		}
		else {
			this.hasChildren = true;
			for(Object obj:childObjects) {
				if(obj instanceof ASTNode) {
					this.childrenNodes.add((ASTNode)obj);
				}
			}	
		}

	}
	
	public Object[] getChildren(ASTNode node) {
	    List list= node.structuralPropertiesForType();
	    for (int i= 0; i < list.size(); i++) {
	        StructuralPropertyDescriptor curr= (StructuralPropertyDescriptor) list.get(i);
	            Object child= node.getStructuralProperty(curr);
	        if (child instanceof List) {
	                return ((List) child).toArray();
	        } else if (child instanceof ASTNode) {
	            return new Object[] { child };
	            }
	    }
        return new Object[0];

	}
	public static int ID = 0;
	public List<ASTNode>  getDirectChildren(ASTNode node) {
		List<ASTNode> resultAstNodes = new ArrayList<ASTNode>();
		List listProperty = node.structuralPropertiesForType();
		boolean hasChildren = false;
		for(int i = 0; i < listProperty.size(); i++){
			StructuralPropertyDescriptor propertyDescriptor = (StructuralPropertyDescriptor) listProperty.get(i);
			if(propertyDescriptor instanceof ChildListPropertyDescriptor){//ASTNode列表
				ChildListPropertyDescriptor childListPropertyDescriptor = (ChildListPropertyDescriptor)propertyDescriptor;
				Object children = node.getStructuralProperty(childListPropertyDescriptor);
				List<ASTNode> childrenNodes = (List<ASTNode>)children;
				for(ASTNode childNode: childrenNodes){
					//获取所有节点
					if(childNode == null)
						continue;
					resultAstNodes.add(childNode);
				}
			}
			else if(propertyDescriptor instanceof ChildPropertyDescriptor){//一个ASTNode
				ChildPropertyDescriptor childPropertyDescriptor = (ChildPropertyDescriptor)propertyDescriptor;
				Object child = node.getStructuralProperty(childPropertyDescriptor);
				ASTNode childNode = (ASTNode)child;
				if(childNode == null)
					continue;
				resultAstNodes.add(childNode);
			}
			else {
				//return new ArrayList<ASTNode>();
			}
		}

		return resultAstNodes;
	}
	
	public String SBT(ASTNode node) {
		String sbtString = new String();
		String typeString = node.getClass().toString().split("\\.")[node.getClass().toString().split("\\.").length-1];
		if(node instanceof Javadoc) {
			return new String();
		}
		List<ASTNode> astNodes = getDirectChildren(node);
		if (astNodes.size() == 0) {
			sbtString = "("+typeString+")"+typeString;
		}
		else {
			sbtString = "("+typeString;
			for(ASTNode astNode :astNodes) {
				sbtString += SBT(astNode);
			}
			sbtString += ")"+typeString;
		}
		return sbtString;
	}


}
