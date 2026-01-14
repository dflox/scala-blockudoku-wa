package services

import model.SimpleUser
import org.pac4j.core.profile.CommonProfile

import javax.inject.{Inject, Singleton}
import scala.collection.concurrent.TrieMap

@Singleton
class UserService @Inject(){
  private val users = TrieMap[String, SimpleUser](
    "admin" -> SimpleUser("admin", "password123"),
    "user" -> SimpleUser("user", "1234")
  )
  
  def addUser(username: String, password: String): Unit = {
    users.put(username, SimpleUser(username, password))
  }
  
  def addUser(simpleUser: SimpleUser) : Unit = {
    users.put(simpleUser.username, simpleUser)
  }

  def findByUsername(username: String): Option[SimpleUser] = users.get(username)
}