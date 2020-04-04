package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/ 
		if (!sc.hasNextLine()) 
			return;
		

		
		root=new TagNode(takeOutBracks(sc.nextLine()),null,null);
		
		Stack <TagNode> stack=new Stack<TagNode>();
		stack.push(root); 
		
		while (sc.hasNextLine()) {

			String line=sc.nextLine();
			boolean isTag=false;
			
			if (line.charAt(0)=='<') {
				isTag=true;
				line=line.substring(1,line.length()-1);
				//System.out.println(line);
			}
			if ((isTag) && line.charAt(0)=='/' && !stack.isEmpty())
				stack.pop();
			
			else {
				TagNode temp=new TagNode(line,null,null); 
				if (!stack.isEmpty()) {
					if (stack.peek().firstChild==null)
						stack.peek().firstChild=temp;
					
					else {
						TagNode child=stack.peek().firstChild;
						while (child.sibling!=null)
							child=child.sibling;
						child.sibling=temp;
					}
					
				}
					
					if (isTag)
						stack.push(temp);
				
			}
		
		}
		
		
		
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) { //CHECK EXCEPTION LATER
		/** COMPLETE THIS METHOD **/
		recReplace(oldTag,newTag,root);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		recBold(row, root);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		
		if (root == null || tag==null)
			return;
		
		while (tagExists(tag,root)) {
			recRemove(tag,root,root.firstChild);
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		if (root==null || word==null || tag==null) 
			return;
		
		else if (tag.equals("em") || tag.equals("b"))
			recAdd(word,tag,root);
			
	
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
	
	private String takeOutBracks(String str) {
		return str.substring(1,str.length()-1);
	}
	private void recReplace(String old, String rep, TagNode curr) {
		if (curr==null)
			return;
		
		else if(curr.tag.equals(old))
			curr.tag=rep;
		
		recReplace(old,rep,curr.firstChild);
		recReplace(old,rep,curr.sibling);
	}
	private void recRemove(String tag, TagNode root, TagNode child) {
		if (child==null || root==null)
			return;
		
		else if (child.tag.equals(tag)) {
			if (tag.equals("ol") || tag.equals("ul")){
				TagNode childChild=child.firstChild;
				while(childChild!=null) {
					if (childChild.tag.equals("li"))
						childChild.tag="p";
					
					childChild=childChild.sibling;
				}
			}
			
			if (root.firstChild==child) {
				root.firstChild=child.firstChild;
				TagNode childChild=child.firstChild;
				
				while (childChild.sibling!=null)
					childChild=childChild.sibling;
				
				childChild.sibling=child.sibling;
			}
			else if (root.sibling==child) {
				TagNode childChild=child.firstChild;
				while (childChild.sibling!=null)
					childChild=childChild.sibling;
				
				childChild.sibling=child.sibling;
				root.sibling=child.firstChild;
			}
			return;
		}
		
		root=child;
		recRemove(tag,root,child.firstChild);
		recRemove(tag,root,child.sibling);
	}
	private boolean tagExists(String tag, TagNode curr) {
		if (curr==null)
			return false;
		
		else if (curr.tag.contentEquals(tag))
			return true;
		
		boolean existsInTree=tagExists(tag, curr.firstChild) || tagExists(tag,curr.sibling);
		
		return existsInTree;
	}
	private void recBold(int row, TagNode curr) {
		if (curr==null)
				return;
		
		if (curr.tag.equals("table")) {
			TagNode rTable=curr.firstChild;
			int count=0;
			while (count<row-1) {
				if (rTable.sibling!=null)
					rTable=rTable.sibling;
				
				else throw new IllegalArgumentException();
				count++;
			}
			TagNode col=rTable.firstChild;
			while (col!=null) {
				TagNode bNode=new TagNode("b", col.firstChild, null);
				col.firstChild=bNode;
				col=col.sibling;
			}
		}
		recBold(row, curr.firstChild);
		recBold(row, curr.sibling);
	}
	private void recAdd(String word, String tag, TagNode curr) {
		if (curr==null)
			return;
		
		String lTag=curr.tag.toLowerCase();
		String lWord=word.toLowerCase();
		
		if (lTag.contains(lWord)) {
			if (lTag.equals(lWord)) {
				String oldWord=curr.tag;
				curr.tag=tag;
				curr.firstChild=new TagNode(oldWord, curr.firstChild,null);
			}
			else {
				TagNode sib=curr.sibling;
				int i=lTag.indexOf(word);
				
				String[] splitArr= {curr.tag.substring(0,i), curr.tag.substring(i,i+word.length()),curr.tag.substring(i+word.length(),lTag.length()),""};
				if (splitArr[2].length()>1 && (checkPunc((splitArr[2].charAt(0))) && (!checkPunc(splitArr[2].charAt(1))))){
					splitArr[3] = "" + splitArr[2].charAt(0);
					splitArr[2] = splitArr[2].substring(1);
				}
				if ((splitArr[2].length() == 0) || (splitArr[2].length() >= 1 && (splitArr[2].charAt(0) == ' ' || checkPunc(splitArr[2].charAt(0))))){
					if ((splitArr[2].length() == 1) && (checkPunc(splitArr[2].charAt(0)))){
						splitArr[1] = splitArr[1] + splitArr[2];
						splitArr[2] = "";
					}
					
					curr.tag=splitArr[0];
					TagNode newChild=new TagNode(splitArr[1]+splitArr[3],null,null);
					curr.sibling=new TagNode(tag,newChild,null);
					
					if (splitArr[2].length()>0) {
						if (sib!=null)
							curr.sibling.sibling=new TagNode(splitArr[2], null, sib);
						else
							curr.sibling.sibling=new TagNode(splitArr[2], null,null);
						
					}
					else if (sib!=null)
						curr.sibling.sibling=sib;
				}
			}
			if (curr.sibling!=null)
				recAdd(word,tag,curr.sibling.sibling);
		}
		else {
			recAdd(word,tag,curr.firstChild);
			recAdd(word,tag,curr.sibling);
		}
	}
	private boolean checkPunc(char c) {
		return ((c == '!') || (c == '?') || (c == '.') || (c == ',') || (c == ';')|| (c == ':'));

	}
}
