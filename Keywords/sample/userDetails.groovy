package sample
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import groovy.json.JsonSlurper

/**
 *
 * @author  Remya
 * class	Common class for the test cases
 * Keywords getUserName, getUserNameList
 *
 */
class userDetails {

	public static JsonSlurper jsonSlurper = new JsonSlurper()

	/**
	 * Keyword to get specified first name
	 * @return
	 */
	@Keyword
	def static void getUserName() {
		try{
			def response = WS.sendRequest(findTestObject('GetSingleUserFirstName'))

			if(response == null) {
				KeywordUtil.markFailed("Key is not present")
			}
			else{
				Map parsedJson = jsonSlurper.parseText(response.getResponseText())
				KeywordUtil.logger.logInfo("Get the first name from the data")
				def firstName = parsedJson.data.first_name

				if(firstName == " ") {
					KeywordUtil.markWarning("No name returned from data")
					throw new AssertionError('ERROR: No name available')
				}
				else {
					println('.......FIRST USERNAME : ' +firstName+'.............')
				}
			}
		}catch(Exception e){
			KeywordUtil.logInfo(e.getMessage())
		}
	}

	/**
	 * Keyword to get all first name list
	 * @return
	 */
	@Keyword
	def static void getUserNameList() {
		try{
			def response = WS.sendRequest(findTestObject('GetUsersList'))
			if(response == null) {
				KeywordUtil.markFailed("Key is not present")
			}

			else{
				Map parsedJson = jsonSlurper.parseText(response.getResponseText())
				KeywordUtil.logInfo("Get the first name list from the data")
				def userNameList = parsedJson.data.first_name
				println('.......EXTRACTED NAME LIST : ' +userNameList+'.............')
			}
		}catch(Exception e){
			KeywordUtil.logInfo(e.getMessage())
		}
	}
}