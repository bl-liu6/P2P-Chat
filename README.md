# P2P Chat

P2P Chat is a simple chat tool implemented based on the Java language
 
## Major elements
### Discovery
- Users can be discovered via a centralized server.  This is to update IP address for peer to peer communications
- The client needs to make sure the IP address in discovery server is correct.  One way of doing it is to have a keep alive packet. If it is not received, the user is not available
- When a user is discovered, clients can send them any updates or new chats(Synchronization)
### Session Initiation
- To meet a new user, you send a message
- A user can block other users.  This means they become not available to you
- If you are connected to any user, you will be receiving messages from the user
- You can mute a user for period of time.  In that case, you will nto see their messages
### Communication and Synchronization
- When a user is discovered, other users can chat with that person
- When a user is discovered, content waiting to be sent from other clients are sent to that user(Synchronization)
### Data Storage
- Data will be stored in the database
## MVP
### Discovery
- You register by making yourself findable by others(as in Telegram or Whatsapp)
### Session Initiation
- Send a message to users who you want to connect with
- Communication and Synchronization
- You can live connect with any of your friends who is available now
- For users who are not available (offline), you can write messages, which are stored on your own client
- The data for offline users will be synchronized when both users are discoverable
### Security
- All is hashed

## Interface
  The UI interface is developed using JavaFX. Because JavaFX has better visual effects than Swing, it was chosen for this project
## Implemented Features
- User registration
- Add friends
- Point-to-point chat
- Group chat
- Real-time message notification and top reminder
- Online/offline reminders
- Friend grouping
##  Technologies Involved
- The main programming language used is Java, and web development technologies such as PHP and front-end languages such as HTML, JS, and CSS are used.
- JDBC is used for database connectivity.
- Google's Gson tool is used for JSON data parsing.
## User Management
The login method is based on the educational administration system account. Before using the application, users need to register on the website by entering their student ID, nickname, etc. The password used for login is the same as the one for the educational administration system. Throughout the registration and login process, the user's password is not saved, ensuring data security.


