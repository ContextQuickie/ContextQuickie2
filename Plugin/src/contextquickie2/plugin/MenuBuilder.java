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
package contextquickie2.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import explorercontextmenu.menu.ExplorerContextMenu;
import explorercontextmenu.menu.ExplorerContextMenuEntry;

public class MenuBuilder extends CompoundContributionItem implements IWorkbenchContribution
{
  private IServiceLocator serviceLocator;
  
  private ExplorerContextMenu contextMenu;
  
  public static final String ParameterCommandId = "ContextQuickie2.Command.CommandId";
  
  public static final String ParameterExplorerContextMenu = "ContextQuickie2.Command.ExplorerContextMenu";

  @Override
  public void initialize(IServiceLocator serviceLocator)
  {
    this.serviceLocator = serviceLocator;
  }

  @Override
  protected IContributionItem[] getContributionItems()
  {
    IContributionItem[] result = null;
    if (this.contextMenu == null)
    {
      Set<String> selectedResources = this.getSelectedResources();
      if (selectedResources.isEmpty() == false)
      {
        this.contextMenu = new ExplorerContextMenu(selectedResources.toArray(new String[selectedResources.size()]));
        contextMenu.setText("Explorer");
      }
    }

    if (this.contextMenu != null)
    {
      IContributionItem menuRoot = this.createMenuEntry(contextMenu);
      if (menuRoot != null)
      {
        result = new IContributionItem[] { menuRoot };
      }
      this.setVisible(true);
    }
    else
    {
      this.setVisible(false);
    }
    
    return result;
  }
  
  private IContributionItem createMenuEntry(ExplorerContextMenuEntry entry)
  {
    IContributionItem result = null;
    
    if (entry.isSeperator())
    {
      result = new Separator();
    }
    else if (entry.getEntries().iterator().hasNext())
    {
      final MenuManager subMenu = new MenuManager(entry.getText(), null, null);
      Iterator<ExplorerContextMenuEntry> iterator = entry.getEntries().iterator();
      
      while (iterator.hasNext())
      {
        subMenu.add(this.createMenuEntry(iterator.next()));
      }
      
      result = subMenu;
    }
    else
    {
      final CommandContributionItemParameter commandParameter = new CommandContributionItemParameter(
        this.serviceLocator, 
        null,
        "ContextQuickie2.Command", 
        CommandContributionItem.STYLE_PUSH);

      // Create map of parameters for the command
      final Map<String, Object> parameters = new HashMap<String, Object>();
      parameters.put(ParameterCommandId, entry.getCommandId());
      parameters.put(ParameterExplorerContextMenu, entry);
      commandParameter.parameters = parameters;
      commandParameter.label = entry.getText();
      
      if (entry.getImageHandle() != 0)
      {
        Image image = Image.win32_new(Display.getCurrent(), SWT.BITMAP, entry.getImageHandle());
        ImageData imageData = image.getImageData();
        imageData.transparentPixel = 0;
        Image transparentImage = new Image(Display.getCurrent(), imageData);
        commandParameter.icon = ImageDescriptor.createFromImage(transparentImage);
      }

      result = new CommandContributionItem(commandParameter);
    }

    return result;
  }
  
  private Set<String> getSelectedResources()
  {
    final Set<String> selectedResources = new HashSet<String>();

    final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null)
    {
      final ISelection selection = window.getSelectionService().getSelection();
      if (selection != null)
      {
        final IAdapterManager adapterManager = Platform.getAdapterManager();

        if ((selection instanceof ITreeSelection) && (selection.isEmpty() == false))
        {
          // Context menu has been opened in a tree view
          for (Object selectedItem : ((IStructuredSelection) selection).toList())
          {
            final IResource resource = adapterManager.getAdapter(selectedItem, IResource.class);
            String path = this.convertIResourceToPath(resource);
            if (path != null)
            {
              selectedResources.add(path);
            }
          }
        }
        else if (selection instanceof TextSelection)
        {
          // Context menu has been opened in an editor
          IEditorPart editor = window.getActivePage().getActiveEditor();
          if (editor != null)
          {
            final IResource resource = adapterManager.getAdapter(editor.getEditorInput(), IResource.class);
            String path = this.convertIResourceToPath(resource);
            if (path != null)
            {
              selectedResources.add(path);
            }
          }
        }
      }
    }

    return selectedResources;
  }
  
  private String convertIResourceToPath(IResource resource)
  {
    String path = null;
    if (resource != null)
    {
      IPath location = resource.getLocation();
      if (location != null)
      {
        path = location.toOSString();
      }
    }
    
    return path;
  }

}