package com.example.audiovisualmanager.database

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.example.audiovisualmanager.model.User
import java.sql.*
import java.util.*
import com.example.audiovisualmanager.model.Game


class MysqlManager {
    companion object {
        val instance = MysqlManager()
        //Declaramos el nombre de la base de datos y su version
        private const val DATABASE_NAME = "AudiovisualManagerDatabase"

        //Declaramos los nombre de las tablas
        private const val TABLE_USER = "UserTable"
        private const val TABLE_USERGAMES = "UserGames"
        private const val TABLE_GAMES = "Games"

        //Declaramos los valores de las columnas
        //TABLA UserTable
        private const val ID = "id"
        private const val NICK_USER = "nick"
        private const val PASSWORD_USER = "password"
        private const val PRIVATE_USER = "private"

        private const val GAMEID = "gameid"
        private const val GAMEVALIDATED = "validated"

        //TABLA PENDING
        private const val GAME_NAME = "name"
        private const val GAME_PLATFORM = "platform"
        private const val GAME_GENRE = "genre"
        private const val GAME_COMPANY = "company"
        private const val GAME_POINTS = "valoration"
        private const val GAME_STATUS = "status"
        private const val GAME_USERID = "userid"
        private const val GAME_IMAGE = "image"


    }

    fun getInstance(): MysqlManager {
        return instance
    }
    private var conn: Connection? = null
    private var username = "android" // provide the username
    private var password = "Android1313!" // provide the corresponding password
    fun getConnection() {
        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        val connectionProps = Properties()
        connectionProps["user"] = username
        connectionProps["password"] = password
        try {
            Class.forName("org.mariadb.jdbc.Driver").newInstance()
            conn = DriverManager.getConnection(
                "jdbc:" + "mariadb" + "://" +
                        "82.223.216.179" +
                        ":" + "3306" + "/" +
                        DATABASE_NAME,
                connectionProps)
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } catch (ex: Exception) {
            // handle any errors
            ex.printStackTrace()
        }
        finally {
            executeMySQLQueryCreation()
        }
    }

    private fun executeMySQLQueryCreation() {
        var stmt: Statement? = null

        try {
            stmt = conn?.createStatement()
            var query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                    + ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + NICK_USER + " varchar(64) NOT NULL, "
                    + PASSWORD_USER + " varchar(64) NOT NULL, "
                    + PRIVATE_USER + " INT NOT NULL DEFAULT 0"
                    + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USERGAMES + "("
                    + GAMEID + " INT NOT NULL, "
                    + GAME_STATUS + " varchar(64) NOT NULL,"
                    + GAME_POINTS + " INT NOT NULL,"
                    + GAME_PLATFORM + " varchar(64) NOT NULL,"
                    + GAME_USERID + " INT NOT NULL" + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_GAMES + "("
                    + GAMEID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + GAME_NAME + " varchar(64) NOT NULL,"
                    + GAME_GENRE + " varchar(64) NOT NULL,"
                    + GAME_COMPANY + " varchar(64) NOT NULL,"
                    + GAMEVALIDATED + " INT NOT NULL DEFAULT 0,"
                    + GAME_IMAGE + " varchar(255)" + ")")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
    }

    fun isValidUser(username: String, password: String): Boolean? {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        var isValid: Boolean? = null
        var stmt: Statement? = null
        var queryResults: ResultSet? = null

        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username' AND $PASSWORD_USER = '$password'")
            queryResults = stmt?.executeQuery(query)

            isValid = queryResults?.next() == true

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (queryResults != null) {
                try {
                    queryResults.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }

        return isValid
    }

    fun addUser(user: User): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val query = ("INSERT INTO $TABLE_USER ($NICK_USER, $PASSWORD_USER) VALUES ('${user.name}', '${user.password}')")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun checkUserExists(username: String): Boolean? {
        if (username.isEmpty()) {
            return false
        }
        var isValid: Boolean? = null
        var stmt: Statement? = null
        var resultSet: ResultSet? = null

        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            resultSet = stmt?.executeQuery(query)

            isValid = resultSet?.next() == true

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }

        return isValid
    }

    fun getGamesPendingByUserid(userid: Int, order: String? = null): ArrayList<Game>? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var games: ArrayList<Game>? = null
        try {
            stmt = conn?.createStatement()
            var query = ("SELECT T1.$GAME_NAME AS $GAME_NAME, T2.$GAME_STATUS AS $GAME_STATUS, T2.$GAME_PLATFORM AS $GAME_PLATFORM," +
                    "T1.$GAMEID AS $GAMEID, T1.$GAME_COMPANY AS $GAME_COMPANY," +
                    "T1.$GAME_GENRE AS $GAME_GENRE, T2.$GAME_POINTS AS $GAME_POINTS, T1.$GAME_IMAGE AS $GAME_IMAGE " +
                    "FROM $TABLE_GAMES T1 INNER JOIN $TABLE_USERGAMES T2 ON T1.$GAMEID = T2.$GAMEID WHERE T2.$GAME_USERID = $userid")
            if (order != null) {
                query += " ORDER BY $order DESC"
            }
            resultSet = stmt?.executeQuery(query)

            games = ArrayList<Game>()
            while (resultSet?.next() == true) {
                games.add(
                    Game(
                        resultSet.getString(GAME_NAME),
                        resultSet.getString(GAME_STATUS),
                        resultSet.getString(GAME_PLATFORM),
                        resultSet.getInt(GAMEID),
                        resultSet.getString(GAME_COMPANY),
                        resultSet.getString(GAME_GENRE),
                        resultSet.getInt(GAME_POINTS),
                        resultSet.getString(GAME_IMAGE),
                    )
                )
            }

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return games
    }

    fun getUserId(username: String): Int? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var userid: Int? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $ID FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            resultSet = stmt?.executeQuery(query)

            if (resultSet?.next() == true) {
                userid = resultSet.getInt(ID)
            }

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return userid

    }

    fun addGameByUserid(game: Game, userid: Int): Boolean {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        val image = if(game.image.isNullOrEmpty()) "" else game.image
        var noError = true
        try {
            stmt = conn?.createStatement()
            var query = "INSERT INTO $TABLE_GAMES ($GAME_NAME, $GAME_IMAGE, $GAME_COMPANY, $GAME_GENRE" +
                    ") VALUES ('${game.name}', '$image', '${game.company}', '${game.genre}')"
            stmt?.executeQuery(query)

            query = "SELECT LAST_INSERT_ID() AS $GAMEID"
            resultSet = stmt?.executeQuery(query)
            var gameid = 0
            if (resultSet?.next() == true) {
                gameid = resultSet.getInt(GAMEID)
            }
            query = "INSERT INTO $TABLE_USERGAMES ($GAMEID, $GAME_STATUS, $GAME_POINTS, $GAME_USERID, $GAME_PLATFORM)" +
                    "VALUES ($gameid, '${game.status}', ${game.valoration}, $userid, '${game.platform}')"
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // handle any errors
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun updateUser(userid: Int, password: String, private : Boolean): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            val isPrivate = if(private) 1 else 0
            val query = ("UPDATE $TABLE_USER SET $PASSWORD_USER = '$password', $PRIVATE_USER = $isPrivate WHERE $ID = $userid")

            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // handle any errors
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun deleteGame(gameid: Int): Boolean {
        var stmt: Statement? = null
        var noError = true
        try {
            stmt = conn?.createStatement()
            var query = ("DELETE FROM $TABLE_USERGAMES WHERE $GAMEID = $gameid")
            stmt?.executeQuery(query)

            query = ("DELETE FROM $TABLE_GAMES WHERE $GAMEID = $gameid")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // handle any errors
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun getGameById(gameid: Int): Game? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var game: Game? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT T1.$GAME_NAME AS $GAME_NAME, T2.$GAME_STATUS AS $GAME_STATUS, T2.$GAME_PLATFORM AS $GAME_PLATFORM," +
                    "T1.$GAMEID AS $GAMEID, T1.$GAME_COMPANY AS $GAME_COMPANY," +
                    "T1.$GAME_GENRE AS $GAME_GENRE, T2.$GAME_POINTS AS $GAME_POINTS, T1.$GAME_IMAGE AS $GAME_IMAGE " +
                    "FROM $TABLE_GAMES T1 INNER JOIN $TABLE_USERGAMES T2 ON T1.$GAMEID = T2.$GAMEID WHERE T1.$GAMEID = $gameid")
            resultSet = stmt?.executeQuery(query)

            if (resultSet?.next() == true) {
                game = Game(
                    resultSet.getString(GAME_NAME),
                    resultSet.getString(GAME_STATUS),
                    resultSet.getString(GAME_PLATFORM),
                    resultSet.getInt(GAMEID),
                    resultSet.getString(GAME_COMPANY),
                    resultSet.getString(GAME_GENRE),
                    resultSet.getInt(GAME_POINTS),
                    resultSet.getString(GAME_IMAGE),
                )
            }

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return game
    }

    fun updateGame(game: Game): Boolean {
        var stmt: Statement? = null
        val image = if(game.image.isNullOrEmpty()) "" else game.image
        var noError = true
        try {
            stmt = conn?.createStatement()
            var query = ("UPDATE $TABLE_GAMES SET $GAME_NAME = '${game.name}', $GAME_IMAGE = '$image', $GAME_COMPANY = '${game.company}', $GAME_GENRE = '${game.genre}' WHERE $GAMEID = ${game.id}")
            stmt?.executeQuery(query)

            query = ("UPDATE $TABLE_USERGAMES SET $GAME_STATUS = '${game.status}', $GAME_POINTS = ${game.valoration}, $GAME_PLATFORM = '${game.platform}' WHERE $GAMEID = ${game.id}")
            stmt?.executeQuery(query)

        } catch (ex: SQLException) {
            // handle any errors
            noError = false
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return noError
    }

    fun getUserList(exception: Int): ArrayList<User>? {
        var stmt: Statement? = null
        var resultSet: ResultSet? = null
        var users: ArrayList<User>? = null
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT * FROM $TABLE_USER" +
                    " WHERE $ID != $exception")
            resultSet = stmt?.executeQuery(query)

            users = ArrayList<User>()
            while (resultSet?.next() == true) {
                users.add(User(
                    resultSet.getString(NICK_USER),
                    resultSet.getString(PASSWORD_USER),
                    resultSet.getInt(PRIVATE_USER),
                    resultSet.getInt(ID)
                ))
            }

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close()
                } catch (sqlEx: SQLException) {
                }
            }
        }
        return users
    }

}