package org.jboss.tools.esb.project.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.JavaFacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.jboss.tools.esb.core.facet.IJBossESBFacetDataModelProperties;
import org.jboss.tools.esb.core.facet.JBossESBFacetDataModelProvider;

public class JBossESBFacetProjectCreationDataModelProvider extends
		FacetProjectCreationDataModelProvider {


	public void init() {
		super.init();

		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel javaFacet = DataModelFactory.createDataModel(new JavaFacetInstallDataModelProvider());
		map.add(javaFacet);
		
		IDataModel esbFacet = DataModelFactory.createDataModel(new JBossESBFacetDataModelProvider());
		map.add(esbFacet);
		String esbSrc = esbFacet.getStringProperty(IJBossESBFacetDataModelProperties.ESB_SOURCE_FOLDER);
		javaFacet.setProperty(IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME, esbSrc);
        
	}


	
	@Override
	public IStatus validate(String propertyName) {
		 
		return super.validate(propertyName);
	}

	
	
}
