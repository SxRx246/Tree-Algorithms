import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class BinarySearchTreeDemo {

    static class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    static class BST {
        Node root;

        void insert(int data) {
            root = insert(root, data);
        }

        private Node insert(Node current, int data) {
            if (current == null) {
                return new Node(data);
            }

            if (data < current.data) {
                current.left = insert(current.left, data);
            } else if (data > current.data) {
                current.right = insert(current.right, data);
            }

            return current;
        }

        boolean search(int data, List<Integer> path) {
            Node current = root;

            while (current != null) {
                path.add(current.data);

                if (data == current.data) {
                    return true;
                }

                if (data < current.data) {
                    current = current.left;
                } else {
                    current = current.right;
                }
            }

            return false;
        }

        void delete(int data) {
            root = delete(root, data);
        }

        private Node delete(Node current, int data) {
            if (current == null) {
                return null;
            }

            if (data < current.data) {
                current.left = delete(current.left, data);
            } else if (data > current.data) {
                current.right = delete(current.right, data);
            } else {

                if (current.left == null) {
                    return current.right;
                }

                if (current.right == null) {
                    return current.left;
                }

                Node successor = current.right;

                while (successor.left != null) {
                    successor = successor.left;
                }

                current.data = successor.data;

                current.right = delete(
                        current.right, successor.data
                );
            }

            return current;
        }

        void inorder() {
            inorder(root);
            System.out.println();
        }

        private void inorder(Node current) {
            if (current == null) {
                return;
            }

            inorder(current.left);
            System.out.print(current.data + " ");
            inorder(current.right);
        }
    }

    static BST bst = new BST();
    static Graph graph = new SingleGraph("BST");
    static Viewer viewer;

    static int positionIndex;

    static void drawTree(List<Integer> highlightedPath) {
        graph.clear();

        graph.setAttribute("ui.stylesheet",
                "node {" +
                        " fill-color: #93c5fd;" +
                        " size: 38px;" +
                        " text-size: 18px;" +
                        " text-alignment: center;" +
                        " text-color: #172554;" +
                        " stroke-mode: plain;" +
                        " stroke-color: #2563eb;" +
                        " stroke-width: 2px;" +
                        "}" +
                        "node.highlight {" +
                        " fill-color: #fbbf24;" +
                        " stroke-color: #d97706;" +
                        "}" +
                        "edge {" +
                        " fill-color: #94a3b8;" +
                        " size: 2px;" +
                        "}"
        );

        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        addAllNodes(bst.root, highlightedPath);

        positionIndex = 0;
        assignPositions(bst.root, 0);

        addAllEdges(bst.root);
    }

    static void addAllNodes(
            Node current,
            List<Integer> highlightedPath) {

        if (current == null) {
            return;
        }

        String nodeId = String.valueOf(current.data);

        org.graphstream.graph.Node visualNode =
                graph.addNode(nodeId);

        visualNode.setAttribute("ui.label", nodeId);

        if (highlightedPath.contains(current.data)) {
            visualNode.setAttribute("ui.class", "highlight");
        }

        addAllNodes(current.left, highlightedPath);
        addAllNodes(current.right, highlightedPath);
    }

    static void assignPositions(Node current, int depth) {
        if (current == null) {
            return;
        }

        assignPositions(current.left, depth + 1);

        String nodeId = String.valueOf(current.data);

        org.graphstream.graph.Node visualNode =
                graph.getNode(nodeId);

        double x = positionIndex * 2.5;
        double y = -depth * 2.0;

        visualNode.setAttribute("xyz", x, y, 0);

        positionIndex++;

        assignPositions(current.right, depth + 1);
    }

    static void addAllEdges(Node current) {
        if (current == null) {
            return;
        }

        String parentId = String.valueOf(current.data);

        if (current.left != null) {
            String leftId = String.valueOf(current.left.data);

            graph.addEdge(
                    parentId + "-" + leftId,
                    parentId,
                    leftId
            );
        }

        if (current.right != null) {
            String rightId = String.valueOf(current.right.data);

            graph.addEdge(
                    parentId + "-" + rightId,
                    parentId,
                    rightId
            );
        }

        addAllEdges(current.left);
        addAllEdges(current.right);
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");

        int[] productIds = {10, 5, 2, 8, 15, 12, 20};

        for (int productId : productIds) {
            bst.insert(productId);
        }

        drawTree(new ArrayList<>());

        viewer = graph.display(false);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== BST VISUAL DEMO =====");
            System.out.println("1. Insert product ID");
            System.out.println("2. Search for product ID");
            System.out.println("3. Delete product ID");
            System.out.println("4. Display sorted IDs");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = readInteger(scanner);

            switch (choice) {
                case 1:
                    System.out.print("Enter product ID: ");
                    int newId = readInteger(scanner);

                    if (bst.search(newId, new ArrayList<>())) {
                        System.out.println("ID already exists.");
                    } else {
                        bst.insert(newId);
                        drawTree(new ArrayList<>());
                        System.out.println("Product inserted!");
                    }
                    break;

                case 2:
                    System.out.print("Enter product ID: ");
                    int searchId = readInteger(scanner);

                    List<Integer> path = new ArrayList<>();
                    boolean found = bst.search(searchId, path);

                    drawTree(path);

                    System.out.println("Search path: " + path);
                    System.out.println(found
                            ? "Product found!"
                            : "Product not found.");
                    break;

                case 3:
                    System.out.print("Enter product ID: ");
                    int deleteId = readInteger(scanner);

                    if (bst.search(deleteId, new ArrayList<>())) {
                        bst.delete(deleteId);
                        drawTree(new ArrayList<>());
                        System.out.println("Product deleted!");
                    } else {
                        System.out.println("Product not found.");
                    }
                    break;

                case 4:
                    System.out.print("Sorted IDs: ");
                    bst.inorder();
                    break;

                case 5:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        System.out.println("Closing BST demo...");

        if (viewer != null) {
            viewer.close();
        }

        scanner.close();

        System.out.println("BST demo closed.");
        System.exit(0);
    }

    static int readInteger(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }

        return scanner.nextInt();
    }
}
