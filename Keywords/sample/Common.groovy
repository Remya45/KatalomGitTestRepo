
package sample
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.logging.KeywordLogger as KeywordLogger
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import groovy.json.JsonSlurper

/**
 * 
 * @author  Inbu Raja E K
 * class	Common class for the test cases
 * Keywords findIDfromMail, findUserById, deleteUserById
 *
 */
public class Common {

	public static JsonSlurper jsonSlurper = new JsonSlurper()

	/**
	 * Create an user with username and job
	 * @param userName
	 * @param job
	 * @return
	 */
	@Keyword
	def CreateUser(String name, String job) {
		KeywordUtil.logger.logInfo("**** Send request and verify the api to add an user " + name + " ****" )
		def response = WS.sendRequestAndVerify(findTestObject('Object Repository/ADD user', [('sName') : name, ('Job') : job]))
		WS.verifyElementPropertyValue(response, 'name', name)
		WS.verifyElementPropertyValue(response, 'job', job)
	}
	/**
	 * Keyword for deleting the user by ID
	 * @param id
	 * @return
	 */
	@Keyword
	def deleteUserById(int id) {
		KeywordUtil.logger.logInfo("**** Send request and verify the api to delete an user ****" )
		def response = WS.sendRequestAndVerify(findTestObject('Object Repository/DEL user by id', [('id') : id]))
	}
	/**
	 * Keyword for getting all the data page wise
	 * @param pageNo - page number information
	 * @return
	 */
	@Keyword
	def GetAllDataByPage(def pageNo) {
		KeywordUtil.logger.logInfo("**** Send request to get the response on page" + pageNo +" ****" )
		WS.sendRequestAndVerify(findTestObject('GET Details on Page',[('pageNo') : pageNo]))
	}

	/**
	 * Keyword for getting all the data page wise
	 * @param pageNo - page number information
	 * @return
	 */
	@Keyword
	def TraverseDataByPage() {
		//sample to verify data driven testing
		def dataValue = 0
		for (def rowNum = 1; rowNum <= TestDataFactory.findTestData('New Test Data').getRowNumbers(); rowNum++) {
			dataValue = TestDataFactory.findTestData("New Test Data").getValue(1,rowNum)
			KeywordUtil.logger.logInfo("**** Send request to get the response on page" + dataValue +" ****" )
			WS.sendRequestAndVerify(findTestObject('GET Details on Page',[('pageNo') : dataValue]))
		}
	}

	/**
	 * Keyword for verify the id of mail
	 * @param strEmail
	 * @return
	 */
	@Keyword
	def FindIdByMailAndVerify(strEmail){
		int out = findIDfromMail(strEmail)
		KeywordUtil.logger.logInfo("**** The ID of email : " + strEmail + " is received as " + out + " ****" )
		if (out != 0) {
			assert strEmail == findUserMailFromId(out)
		}
		else{
			KeywordUtil.logger.logError("**** The ID of email : " + strEmail + " could not be found." )
		}
	}

	/**
	 * Keyword to find a record that matches the e-mailID and return its ID
	 * @param strMailID
	 * @return
	 */
	@Keyword
	def int findIDfromMail(String strMailID) {
		//Get the first page response to find the tatal pages
		KeywordUtil.logger.logInfo("**** Send request to get the total number of pages ****" )
		def response = WS.sendRequestAndVerify(findTestObject('GET Details on Page', [('pageNo') : 1]))
		Map parsedJson = jsonSlurper.parseText(response.getResponseText())
		int totalPages = parsedJson.total_pages
		int foundId = 0 //initialize foundId variable
		KeywordUtil.logger.logInfo("**** The total no. of pages are : " + totalPages + " ****" )

		//iterate through all available pages to find the ID corresponding to the email
		for (def i = 1; i <= totalPages; i++){
			KeywordUtil.logger.logInfo("**** Send request to get the contents of page" + i + " ****" )
			response = WS.sendRequestAndVerify(findTestObject('GET Details on Page', [('pageNo') : i]))
			parsedJson = jsonSlurper.parseText(response.getResponseText())

			//search for the email on parsed data array
			def arrData = parsedJson.data
			for(def data : arrData) {
				if(data.email == strMailID) {
					foundId = data.id
					break
				}
			}
		}

		if (foundId == 0) {
			KeywordUtil.logger.logError("**** The searched email : " + strMailID + " could not be found." )
			KeywordUtil.markFailed("email ID is not present")
		}
		else {
			KeywordUtil.logger.logInfo("**** The searched email : " + strMailID + " is found on ID " + foundId +".****" )
		}

		return foundId
	}

	/**
	 * Keyword for finding an User by ID
	 * @param id	-	The ID of the user to be deleted
	 * @return		- 	Returns the email ID of the ID 
	 */
	@Keyword
	def String findUserMailFromId(int id) {
		KeywordUtil.logger.logInfo("**** Send request to get the user details with ID " + id + " ****" )
		def response = WS.sendRequestAndVerify(findTestObject('Object Repository/GET user by id', [('id') : id]))
		Map parsedJson = jsonSlurper.parseText(response.getResponseText())
		def strEmailID = null //initialize string for getting mailID

		//confirm that the Json is not blank NULL
		if(parsedJson.keySet().contains("data")) {

			//get the email ID of the requested user(id)
			strEmailID = parsedJson.data.email
			KeywordUtil.logInfo("email of the id is " + strEmailID)
			if(strEmailID == null) {
				KeywordUtil.logger.logError("**** The email for ID : " + id + " could not be found." )
				KeywordUtil.markFailed("Key is not present")
				return "Key is not present"
			}
			KeywordUtil.logger.logInfo("**** The searched ID : " + id + " is found with mail " + strEmailID +"****" )
			return strEmailID
		}

	}

	@Keyword
	def int findIDOfProperty(String strProperty, def propertyValue) {

		//Get the first page response to find the tatal pages
		KeywordUtil.logger.logInfo("**** Send request to get the total number of pages ****" )
		def response = WS.sendRequestAndVerify(findTestObject('GET Details on Page', [('pageNo') : 1]))
		def parsedJson = jsonSlurper.parseText(response.getResponseText())
		int totalPages = parsedJson.total_pages
		int foundId = 0 //initialize foundId variable
		KeywordUtil.logInfo("total pages " + totalPages)

		//iterate through all available pages to find the ID corresponding to the email
		for (def i = 1; i <= totalPages; i++){
			KeywordUtil.logger.logInfo("**** Send request to get the contents of page" + i + " ****" )
			response = WS.sendRequestAndVerify(findTestObject('GET Details on Page', [('pageNo') : i]))
			parsedJson = jsonSlurper.parseText(response.getResponseText())

			//search for the email on parsed data array
			def arrData = parsedJson.data
			for(def data : arrData) {
				KeywordUtil.logInfo("email id on pages " + data.get(strProperty))
				if(data.get(strProperty) == propertyValue) {
					foundId = data.id
					break
				}
			}
		}

		if (foundId == 0) {
			KeywordUtil.logger.logError("**** The searched property : " + strProperty + " could not be found." )
			KeywordUtil.markFailed("Requested property is not present")
		}
		else {
			KeywordUtil.logger.logInfo("**** The searched property : " + strProperty + " is found on ID " + foundId +".****" )
		}

		return foundId

	}
}