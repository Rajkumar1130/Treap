import java.util.*;
public class Treap<K extends Comparable<K>> {
    private TreapNode<K> root = null;

    public static class TreapNode<K extends Comparable<K>> {
        private K key;
        private double priority;
        private TreapNode<K> leftChild;
        private TreapNode<K> rightChild;
        public TreapNode(K key, double priority) {
            this.key = key;
            this.priority = priority;
            this.leftChild = null;
            this.rightChild = null;
        }

        public K getKey() {
            return key;
        }

        public double getPriority() {
            if (key == null){
                return 0.0;
            }
            return priority;
        }

        public TreapNode<K> leftChild() {
            return leftChild;
        }

        public TreapNode<K> rightChild() {
            return rightChild;
        }


        public boolean isLeaf() {
            if (leftChild == null && rightChild == null){
                return true;
            }
            return false;
        }

    }

    public static class TreapNavigator<K extends Comparable<K>> {
        private int tabs = 0;

        public void visit(TreapNode<K> node) {
            if (node != null) {
                tabs += 1;
                visit(node.leftChild());
                tabs -= 1;
                process(node);
                tabs += 1;
                visit(node.rightChild());
                tabs -= 1;
            }
        }

        public void process(TreapNode<K> node) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < tabs; i++) {
                builder.append("      ");
            }
            builder.append(node.getKey() + "(" + node.getPriority() + ")");
            System.out.println(builder.toString());
        }
    }

    public Treap() {}
    public void insert(K key){
        insert(key,(int)(Math.random()*100));
    }
    public void insert(K key, double priority) {
        TreapNode<K> Node = new TreapNode<K>(key,priority);
        if (root != null){
            insertChild(Node, null,root);
        }else {
            root = Node;
        }
    }
    private void insertChild( TreapNode<K> Node, TreapNode<K> ParentNode, TreapNode<K> CurrentNode){
        if (Node.key.compareTo(CurrentNode.key) < 0){
            if (CurrentNode.leftChild == null){
                CurrentNode.leftChild = Node;
            }else {
                insertChild(Node, CurrentNode,CurrentNode.leftChild);
            }
            if (Node.priority > CurrentNode.priority){
                CurrentNode = RightRotation(CurrentNode);
                if(ParentNode == null) {
                    root = CurrentNode;
                } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0){
                    ParentNode.leftChild = CurrentNode;
                }else {
                    ParentNode.rightChild = CurrentNode;
                }
            }
        }else if (Node.key.compareTo(CurrentNode.key) > 0){
            if (CurrentNode.rightChild == null){
                CurrentNode.rightChild = Node;
            }
            else {
                insertChild(Node, CurrentNode,CurrentNode.rightChild);
            }
            if (Node.priority > CurrentNode.priority){
                CurrentNode = LeftRotation(CurrentNode);
                if(ParentNode == null) {
                    root = CurrentNode;
                } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0){
                    ParentNode.leftChild = CurrentNode;
                }else {
                    ParentNode.rightChild = CurrentNode;
                }
            }
        }else {
            CurrentNode.priority = Node.priority;
            if (CurrentNode.leftChild != null && CurrentNode.rightChild != null){
                if ( CurrentNode.priority < CurrentNode.leftChild.getPriority() || CurrentNode.getPriority() < CurrentNode.rightChild.getPriority()){
                    if (CurrentNode.getPriority() < CurrentNode.leftChild.getPriority()){
                        CurrentNode = RightRotation(CurrentNode);
                        if(ParentNode == null) {
                            root = CurrentNode;
                        } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0){
                            ParentNode.leftChild = CurrentNode;
                        }else {
                            ParentNode.rightChild = CurrentNode;
                        }
                    }else {
                        CurrentNode = LeftRotation(CurrentNode);
                        if(ParentNode == null) {
                            root = CurrentNode;
                        } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0){
                            ParentNode.leftChild = CurrentNode;
                        }else {
                            ParentNode.rightChild = CurrentNode;
                        }
                    }
                }
            }
        }
    }

    public TreapNode<K> RightRotation(TreapNode<K> node){
        TreapNode<K> TempNode = node.leftChild();
        node.leftChild = TempNode.rightChild();
        TempNode.rightChild = node;
        return TempNode;
    }
    public TreapNode<K> LeftRotation(TreapNode<K> node){
        TreapNode<K> TempNode = node.rightChild();
        node.rightChild = TempNode.leftChild();
        TempNode.leftChild = node;
        return TempNode;
    }

    public double findKey(K key) {
        TreapNode<K> CurrentNode = root;
        while (CurrentNode != null){
            if (key.compareTo(CurrentNode.key) == 0){
                return CurrentNode.priority;
            } else if (key.compareTo(CurrentNode.key) < 0) {
                CurrentNode = CurrentNode.leftChild;
            }else {
                CurrentNode = CurrentNode.rightChild;
            }
        }
        return 0;
    }

    public Treap<K> split(K key) {
        Treap<K> newTreap = new Treap<>();

        splitHelper(newTreap, root, key);
        if(root.key.compareTo(key) > 0) {
            newTreap.insert(root.key, root.priority);
            delete(root.key);
        }
        return newTreap;
    }
    public void splitHelper(Treap<K> newTreap, TreapNode<K> CurrentNode, K key)
    {
        if (CurrentNode != null)
        {
            splitHelper(newTreap, CurrentNode.leftChild, key);
            splitHelper(newTreap, CurrentNode.rightChild, key);
            if (CurrentNode.leftChild != null) {
                if(CurrentNode.leftChild.key.compareTo(key) > 0) {
                    newTreap.insert(CurrentNode.leftChild.key, CurrentNode.leftChild.priority);
                    delete(CurrentNode.leftChild.key);
                }
            }
            if (CurrentNode.rightChild != null) {
                if (CurrentNode.rightChild.key.compareTo(key) > 0) {
                    newTreap.insert(CurrentNode.rightChild.key, CurrentNode.rightChild.priority);
                    delete(CurrentNode.rightChild.key);
                }
            }

        }
    }


    public void join(Treap<K> t1) {
        if (t1.root == null) {
            return;
        }
        if (root == null) {
            root = t1.root;
            return;
        }
        TreapNode<K> maxNode = getMax(root);
        root = joinHelper(t1.root, maxNode,root);

    }

    private TreapNode<K> joinHelper(TreapNode<K> right, TreapNode<K> maxNode, TreapNode<K> left) {
        if (right == null) {
            return left;
        }if (right.key.compareTo(maxNode.key) < 0){
            left.rightChild = joinHelper(right, maxNode,left.rightChild);
            if (left.rightChild.priority > left.priority) {
                left = LeftRotation(left);
            }
            return left;
        }
        else if (right.key.compareTo(maxNode.key) > 0){
            right.leftChild = joinHelper( right.leftChild, maxNode,left);
            if (right.leftChild.priority > right.priority) {
                right = RightRotation(right);
            }
            return right;
        }
        return left;
    }

    private TreapNode<K> getMax(TreapNode<K> node) {
        if (node.rightChild == null) {
            return node;
        }
        return getMax(node.rightChild);
    }


    public int size(){
        return sizeHelper(root);
    }private int sizeHelper(TreapNode<K> CurrentNode){
        if (CurrentNode == null){
            return 0;
        }
        return  1 + sizeHelper(CurrentNode.rightChild) + sizeHelper(CurrentNode.leftChild);
    }

    public double delete(K key) {
        TreapNode<K> CurrentNode = root;
        TreapNode<K> ParentNode = null;
        while (CurrentNode != null){
            if (key.compareTo(CurrentNode.key) == 0){
                break;
            } else if (key.compareTo(CurrentNode.key) < 0) {
                ParentNode = CurrentNode;
                CurrentNode = CurrentNode.leftChild;
            } else {
                ParentNode = CurrentNode;
                CurrentNode = CurrentNode.rightChild;
            }
        }
        if (CurrentNode == null){
            return 0.0;
        }
        if (CurrentNode.leftChild == null){
            if (ParentNode == null){
                root = CurrentNode.rightChild;
            } else if (key.compareTo(ParentNode.key) < 0) {
                ParentNode.leftChild = CurrentNode.rightChild;
            }else {
                ParentNode.rightChild = CurrentNode.rightChild;
            }
        } else if (CurrentNode.rightChild == null) {
            if (ParentNode == null){
                root = CurrentNode.leftChild;
            } else if (key.compareTo(ParentNode.key) > 0) {
                ParentNode.rightChild = CurrentNode.leftChild;
            }else {
                ParentNode.leftChild = CurrentNode.leftChild;
            }
        } else if (!CurrentNode.isLeaf() && CurrentNode.leftChild.priority < CurrentNode.rightChild.priority) {
            CurrentNode = LeftRotation(CurrentNode);
            if (ParentNode == null){
                root = CurrentNode;
            } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0) {
                ParentNode.leftChild = CurrentNode;
            } else {
                ParentNode.rightChild = CurrentNode;
            }
            assert ParentNode != null;
            delete(CurrentNode.leftChild.key);

        }else {
            CurrentNode = RightRotation(CurrentNode);
            if (ParentNode == null){
                root = CurrentNode;
            } else if (CurrentNode.key.compareTo(ParentNode.getKey()) < 0) {
                ParentNode.leftChild = CurrentNode;
            } else {
                ParentNode.rightChild = CurrentNode;
            }
            assert ParentNode != null;
            delete(CurrentNode.rightChild.key);
        }
        return CurrentNode.priority;
    }
    public void display(String label) {
        System.out.println("Printing treap " + label + " ...");
        new TreapNavigator<K>().visit(this.root);
        System.out.println("Size of tree is: " +size());
    }

    public static int getOption(Scanner scanner) {
        boolean valid = false;
        int option = 0;
        do {
            System.out.print("Enter one of the following options");
            System.out.print("(0 -- Insert(Only key),1 -- InsertWithPriority, 2 -- Remove, 3 -- Find, 4 -- Split, "+
                    "5 -- Join, 6 --- Display, 7 --- Exit):");
            try {
                option = scanner.nextInt();
                if (option >= 0 && option <= 7) {
                    valid = true;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } while (!valid);
        return option;
    }


    private static void testStringKeyValues(Scanner scanner) {
        Treap<String> treap = new Treap<>();
        boolean needed = true;
        Treap<String> lastSplitOutput = null;
        while (needed) {
            int option = getOption(scanner);
            switch(option) {
                case 0:
                    System.out.print("Enter string key value only to be inserted separated by new line: ");
                    String key = scanner.next();
                    treap.insert(key);
                    break;
                case 1:
                    System.out.print("Enter string key and priority value to be inserted separated by new line: ");
                    key = scanner.next();
                    double priority = scanner.nextDouble();
                    treap.insert(key, priority);
                    break;
                case 2:
                    System.out.print("Enter string key to be removed: ");
                    key = scanner.next();
                    double val = treap.delete(key);
                    if (val == 0.0) {
                        System.out.println("Key " + key + " does not exist");
                    } else {
                        System.out.println("Priority for removed key " + key + " = " + val);
                    }
                    break;
                case 3:
                    // find operation
                    System.out.print("Enter string key to look up: ");
                    key = scanner.next();
                    val = treap.findKey(key);
                    if (val == 0.0) {
                        System.out.println("Key " + key + " does not exist");
                    } else {
                        System.out.println("Priority for key " + key + " = " + val);
                    }
                    break;

                case 4:
                    // split operation
                    System.out.print("Enter string key for split operation: ");
                    key = scanner.next();
                    lastSplitOutput = treap.split(key);
                    lastSplitOutput.display("split output");
                    break;
                case 5:
                    // join operation
                    if (lastSplitOutput == null)  {
                        System.out.print("Do split operation first");
                    } else {
                        treap.join(lastSplitOutput);
                        lastSplitOutput = null;
                    }
                    break;
                case 6:
                    break;
                case 7:
                    System.out.println("Exiting...");
                    System.exit(0);
            }
            treap.display("treap");
        }
    }

    public static void main(String [] args) throws Exception {
        testStringKeyValues(new Scanner(System.in));
    }
}