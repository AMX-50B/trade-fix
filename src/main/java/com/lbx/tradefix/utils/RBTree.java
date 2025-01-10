package com.lbx.tradefix.utils;

import java.util.List;

/**
 *
 * @author LiuY
 * @date 2024/12/13
 **/
public class RBTree {
    private static final boolean RED = true; // 定义红色常量
    private static final boolean BLACK = false;// 定义黑色常量
    private static List list;

    // 定义树节点类
    private class Node {
        int key; // 节点键值
        Node left, right; // 左右子节点
        boolean color; // 节点颜色

        // 节点构造函数
        public Node(int key, boolean color) {
            this.key = key;
            this.color = color;
        }
    }

    private Node root; // 树的根节点

    // 判断节点是否为红色
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // 左旋操作
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // 右旋操作
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    // 颜色翻转操作
    private void flipColors(Node h) {
        h.color = RED;
        h.left.color = BLACK;
        h.right.color = BLACK;
    }

    // 向树中插入键值对
    public void put(int key) {
        root = put(root, key); // 从根节点开始插入
        root.color = BLACK; // 根节点始终为黑色
    }

    // 递归插入键值对
    private Node put(Node h, int key) {
        if (h == null) return new Node(key, RED); // 如果节点为空，创建新节点并着色为红色

        // 根据键值大小决定插入到左子树还是右子树
        if (key < h.key) h.left = put(h.left, key);
        else if (key > h.key) h.right = put(h.right, key);
        else h.key = key; // 如果键值相等，更新节点键值

        // 插入后调整树结构以保持红黑树性质
        if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h); // 右子树红色而左子树非红色，左旋
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h); // 连续左子树红色，右旋
        if (isRed(h.left) == RED && isRed(h.right)) flipColors(h); // 左右子树均为红色，颜色翻转

        return h; // 返回调整后的节点
    }

    void preOrder(Node root) {
        if (root == null)
            return;
        // 访问优先级：根节点 -> 左子树 -> 右子树
        list.add(root.key);
        preOrder(root.left);
        preOrder(root.right);
    }

    /* 中序遍历 */
    void inOrder(Node root) {
        if (root == null)
            return;
        // 访问优先级：左子树 -> 根节点 -> 右子树
        inOrder(root.left);
        list.add(root.key);
        inOrder(root.right);
    }

    /* 后序遍历 */
    void postOrder(Node root) {
        if (root == null)
            return;
        // 访问优先级：左子树 -> 右子树 -> 根节点
        postOrder(root.left);
        postOrder(root.right);
        list.add(root.key);
    }
}
