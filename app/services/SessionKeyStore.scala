package services

class SessionKeyStore {
  private var sessionKey: String = ""

  def setSessionKey(key: String): SessionKeyStore = {
    sessionKey = key
    this
  }

  def getSessionKey: String = sessionKey
}
