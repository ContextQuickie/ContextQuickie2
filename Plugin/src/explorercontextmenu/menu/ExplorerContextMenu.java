/***********************************************************************************************************************
 MIT License

 Copyright(c) 2020 Roland Reinl

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files(the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and /or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions :

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
***********************************************************************************************************************/

package explorercontextmenu.menu;

public class ExplorerContextMenu extends ExplorerContextMenuEntry
{
  /**
   * Constructor.
   * 
   * @param paths
   *      The paths for which the context menu is created.
   * @param createDefaultMenu
   *      A value indicating whether the default menu is created or not.
   * @param whitelist
   *      A list containing all UUIDs for extended context menu entries which are loaded.
   * @param blacklist
   *      A list containing all UUIDs for extended context menu entries which are not loaded.
   */
  public ExplorerContextMenu(String[] paths, boolean createDefaultMenu, String[] whitelist, String[] blacklist)
  {
    this.getEntries(paths, createDefaultMenu, whitelist, blacklist);
  }

  /**
   * Loads the required native library.
   * 
   * @param libraryDirectory
   *      The directory which contains the native library.
   */
  public static void loadNativeLibrary(String libraryDirectory)
  {
    final String archDataModel = System.getProperty("sun.arch.data.model");
    System.loadLibrary(libraryDirectory + "/ExplorerContextMenu.Java." + archDataModel);
  }

  @Override
  protected void finalize() throws Throwable
  {
    this.deleteUnmanagedInstance();
  }

  private native void getEntries(String[] paths, boolean createDefaultMenu, String[] whitelist, String[] blacklist);

  private native void deleteUnmanagedInstance();
}
