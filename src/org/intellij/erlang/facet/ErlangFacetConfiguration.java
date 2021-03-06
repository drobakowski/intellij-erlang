package org.intellij.erlang.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.erlang.facet.ui.ErlangFacetEditor;
import org.intellij.erlang.jps.model.ErlangModuleExtensionProperties;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author savenko
 */
public class ErlangFacetConfiguration implements FacetConfiguration, PersistentStateComponent<ErlangModuleExtensionProperties> {
  private ErlangModuleExtensionProperties myState = new ErlangModuleExtensionProperties();

  @Override
  public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
    return new FacetEditorTab[] {new ErlangFacetEditor(editorContext, this)};
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
  }

  @Nullable
  @Override
  public ErlangModuleExtensionProperties getState() {
    return myState;
  }

  @Override
  public void loadState(ErlangModuleExtensionProperties state) {
    myState = state;
  }

  public List<String> getIncludePaths() {
    return myState.myIncludePaths;
  }

  public void setIncludePaths(List<String> includePaths) {
    myState.myIncludePaths = includePaths;
  }
  
  public void addIncludeDirectories(@NotNull Module module) {
    VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();

    List<String> updatedIncludePaths = new ArrayList<String>(getIncludePaths().size() + contentRoots.length);
    updatedIncludePaths.addAll(getIncludeFolderPaths(module));
    if (updatedIncludePaths.isEmpty()) return;

    for (String includePath : getIncludePaths()) {
      if (!updatedIncludePaths.contains(includePath)) {
        updatedIncludePaths.add(includePath);
      }
    }
    setIncludePaths(updatedIncludePaths);
  }

  @NotNull 
  public static List<String> getIncludeFolderPaths(@NotNull Module module) {
    List<String> includeFolderPaths = new ArrayList<String>();
    VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
    for (VirtualFile contentRoot : contentRoots) {
      VirtualFile includeDirectory = VfsUtil.findRelativeFile(contentRoot, "include");
      if (includeDirectory != null) {
        includeFolderPaths.add(includeDirectory.getPath());
      }
    }
    return includeFolderPaths;
  }

  public List<String> getParseTransforms() {
    return myState.myParseTransforms;
  }

  public void setParseTransforms(List<String> parseTransforms) {
    myState.myParseTransforms = parseTransforms;
  }
}
