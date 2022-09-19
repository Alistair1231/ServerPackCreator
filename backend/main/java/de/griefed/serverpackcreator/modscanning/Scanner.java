/* Copyright (C) 2022  Griefed
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
package de.griefed.serverpackcreator.modscanning;

/**
 * Small interface for making sure each scanner has a {@code scan}-method with a given format.
 *
 * @param <T> The return-type of the implementation. Determines the return-type of the
 *            {@code scan }-method, thus allowing you to control what the said method returns.
 * @param <U> The parameter-type of the implementation. Determines the parameter-type of the
 *            {@code scan}-method, thus allowing you to control what said method receives.
 * @author Grefed
 */
interface Scanner<T, U> {

  T scan(U jarFiles);
}
