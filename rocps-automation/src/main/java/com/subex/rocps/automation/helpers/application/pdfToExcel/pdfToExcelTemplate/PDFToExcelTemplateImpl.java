package com.subex.rocps.automation.helpers.application.pdfToExcel.pdfToExcelTemplate;

import com.subex.automation.helpers.component.ButtonHelper;
import com.subex.automation.helpers.component.ComboBoxHelper;
import com.subex.automation.helpers.component.ElementHelper;
import com.subex.automation.helpers.component.FileHelper;
import com.subex.automation.helpers.component.GenericHelper;
import com.subex.automation.helpers.component.GridHelper;
import com.subex.automation.helpers.component.MouseHelper;
import com.subex.automation.helpers.component.TextBoxHelper;
import com.subex.automation.helpers.util.FailureHelper;
import com.subex.rocps.automation.helpers.application.genericHelpers.PSGenericHelper;
import com.subex.rocps.automation.helpers.selenium.PSAcceptanceTest;
import com.subex.rocps.automation.utils.PSStringUtils;

public class PDFToExcelTemplateImpl extends PSAcceptanceTest
{
	PSGenericHelper genObj = new PSGenericHelper();
	public void headerDetails( String templateName, String summaryStartPage, String summaryEndPage, String footerLines, String skipPagesLast, String excelPath, String pdfPath, String pdfFilename, String excelExtension  ) throws Exception
	{
		TextBoxHelper.type( "PS_Detail_pdfExlTemp_name", templateName );
		TextBoxHelper.type( "PS_Detail_pdfExlTemp_summaryStart", summaryStartPage );
		TextBoxHelper.type( "PS_Detail_pdfExlTemp_summaryEnd", summaryEndPage );
		TextBoxHelper.type( "PS_Detail_pdfExlTemp_footerLines", footerLines );
		TextBoxHelper.type( "PS_Detail_pdfExlTemp_skipPages", skipPagesLast );
		GenericHelper.selectDataDir( "PS_Detail_pdfExlTemp_outputPath", excelPath, configProp.getThirdLevelDelimiter() );
		ButtonHelper.click( "PS_Detail_pdfExlTemp_pdfFilename" );
		FileHelper.fileUploadRobot( "PS_Detail_pdfExlTemp_uploadTrigger", automationPath + configProp.getProperty( pdfPath ) + pdfFilename );
		ButtonHelper.click( "PS_Detail_pdfExlTemp_uploadButton" );
		GenericHelper.waitForLoadmask( searchScreenWaitSec );
		ComboBoxHelper.select( "ppxtExtension_gwt_uid_", excelExtension );
		GenericHelper.waitForLoadmask( searchScreenWaitSec );
		

	}
	

	public void clickOnSave() throws Exception
	{
		ButtonHelper.click( "PS_Detail_pdfExlTemp_saveButton" );
		GenericHelper.waitForLoadmask( searchScreenWaitSec );
		GenericHelper.waitForLoadmask( searchScreenWaitSec );
	}
	
	public boolean validateTemplateCreation(String templateName) throws Exception
	{
		return genObj.isDataPresent( templateName, "Template Name" );

	}
	
	public void clickConvertButton() throws Exception
	{
		ButtonHelper.click( "PS_Detail_pdfExlTemp_convertButton" );

	}

	public void sheetAndColumns(String sheetType, String columns) throws Exception
	{
		ComboBoxHelper.select( "PS_Detail_pdfExlTemp_sheet", sheetType );
		PSStringUtils strObj = new PSStringUtils();
		String[] columnList = strObj.stringSplitFirstLevel( columns );
		for ( int i = 0; i < columnList.length; i++ )
		{
			ButtonHelper.click( "PS_Detail_pdfExlTemp_addButton" );
			GridHelper.clickRow( "PS_Detail_pdfExlTemp_grid", i + 1, 1 );
			TextBoxHelper.type( "colEditor", columnList[i] );

		}
	}
	
}
