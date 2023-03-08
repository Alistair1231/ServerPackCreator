/* Copyright (C) 2023  Griefed
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 * The full license can be found at https:github.com/Griefed/ServerPackCreator/blob/main/LICENSE
 */
package de.griefed.serverpackcreator.gui.filebrowser.view.renderer

import de.griefed.serverpackcreator.gui.filebrowser.model.FileNode
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

/**
 * TODO docs
 */
class FileNameComparator :Comparator<TreeNode> {
    override fun compare(o1: TreeNode, o2: TreeNode): Int {
        val t1 = (o1 as DefaultMutableTreeNode).userObject
        val t2 = (o2 as DefaultMutableTreeNode).userObject
        val f1 = (t1 as FileNode).file
        val f2 = (t2 as FileNode).file
        return f1.name.compareTo(f2.name)
    }
}