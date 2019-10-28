package org.checkerframework.framework.util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * TreePathCacher is a TreeScanner that creates and caches a TreePath for a target Tree.
 *
 * <p>This class replicates some logic from TreePath.getPath but also adds caching to all
 * intermediate TreePaths that are generated. The intermediate TreePaths are reused when other
 * targets have overlapping paths.
 */
public class TreePathCacher extends TreeScanner<TreePath, Tree> {

    private final Map<Tree, TreePath> foundPaths = new HashMap<>(32);

    private final Function<Tree, TreePath> computeValue =
            (Tree key) -> new TreePath(this.path, key);

    /**
     * The TreePath of the previous tree scanned. It is always set back to null after a scan has
     * completed.
     */
    private TreePath path;

    /**
     * @param target the tree to search for
     * @return true if the tree is cached
     */
    public boolean isCached(Tree target) {
        return foundPaths.containsKey(target);
    }

    /**
     * @param target the tree to add
     * @param path the path to cache
     */
    public void addPath(Tree target, TreePath path) {
        foundPaths.put(target, path);
    }

    /**
     * Return the TreePath for a Tree.
     *
     * <p>This method uses try/catch and the Result Error for control flow to stop the superclass
     * from scanning other subtrees when target is found.
     *
     * @param root the compilation unit to search in
     * @param target the target tree to look for
     * @return the TreePath corresponding to target, or null if target is not found in the
     *     compilation root
     */
    public TreePath getPath(CompilationUnitTree root, Tree target) {
        TreePath cachedPath = foundPaths.get(target);
        if (cachedPath != null) {
            return cachedPath;
        }

        TreePath path = new TreePath(root);
        if (path.getLeaf() == target) {
            return path;
        }

        try {
            this.scan(path, target);
        } catch (Result result) {
            return result.path;
        }
        // If a path wasn't found, cache null so the whole compilation unit isn't searched again.
        foundPaths.put(target, null);
        return null;
    }

    private static class Result extends Error {
        private static final long serialVersionUID = 4948452207518392627L;
        TreePath path;

        Result(TreePath path) {
            this.path = path;
        }
    }

    public void clear() {
        foundPaths.clear();
    }

    /** Scan a single node. The current path is updated for the duration of the scan. */
    @Override
    public TreePath scan(Tree tree, Tree target) {
        TreePath prev = this.path;

        if (tree == null) {
            this.path = null;
        } else {
            this.path = foundPaths.computeIfAbsent(tree, this.computeValue);
        }

        if (tree == target) {
            throw new Result(this.path);
        }
        try {
            return super.scan(tree, target);
        } finally {
            this.path = prev;
        }
    }
}
