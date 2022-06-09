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
        private const val TABLE_PENDING = "Games"

        //Declaramos los valores de las columnas
        //TABLA UserTable
        private const val ID = "id"
        private const val NICK_USER = "nick"
        private const val PASSWORD_USER = "password"

        //TABLA PENDING
        private const val GAME_NAME = "name"
        private const val GAME_PLATFORM = "platform"
        private const val GAME_STATUS = "status"
        private const val GAME_USERID = "userid"
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

    fun executeMySQLQueryCreation() {
        var stmt: Statement? = null

        try {
            stmt = conn?.createStatement()
            var query = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
                    + ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + NICK_USER + " varchar(64) NOT NULL,"
                    + PASSWORD_USER + " varchar(64) NOT NULL" + ")")
            stmt?.executeQuery(query)

            query = ("CREATE TABLE IF NOT EXISTS " + TABLE_PENDING + "("
                    + ID + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + GAME_NAME + " varchar(64) NOT NULL,"
                    + GAME_PLATFORM + " varchar(64) NOT NULL,"
                    + GAME_STATUS + " varchar(64) NOT NULL,"
                    + GAME_USERID + " INT NOT NULL" + ")")
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

    fun isValidUser(username: String, password: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            return false
        }
        var isValid = false
        var stmt: Statement? = null
        val resultSet: ResultSet? = null

        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username' AND $PASSWORD_USER = '$password'")
            val resultSet = stmt?.executeQuery(query)

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

    fun addUser(user: User) {
        var stmt: Statement? = null
        try {
            stmt = conn?.createStatement()
            val query = ("INSERT INTO $TABLE_USER ($NICK_USER, $PASSWORD_USER) VALUES ('${user.name}', '${user.password}')")
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

    fun checkUserExists(username: String): Boolean {
        if (username.isEmpty()) {
            return false
        }
        var isValid = false
        var stmt: Statement? = null
        val resultSet: ResultSet? = null

        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $NICK_USER FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            val resultSet = stmt?.executeQuery(query)

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

    fun getGamesPendingByUserid(userid: Int): ArrayList<Game> {
        var stmt: Statement? = null
        val resultSet: ResultSet? = null
        val games = ArrayList<Game>()
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT * FROM $TABLE_PENDING WHERE $GAME_USERID = $userid")
            val resultSet = stmt?.executeQuery(query)

            while (resultSet?.next() == true) {
                games.add(
                    Game(
                    resultSet.getString(GAME_NAME),
                    resultSet.getString(GAME_STATUS),
                    resultSet.getString(GAME_PLATFORM)
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

    fun getUserId(username: String): Int {
        var stmt: Statement? = null
        val resultSet: ResultSet? = null
        var userid = -1
        try {
            stmt = conn?.createStatement()
            val query = ("SELECT $ID FROM $TABLE_USER WHERE $NICK_USER = '$username'")
            val resultSet = stmt?.executeQuery(query)

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

    fun addGameByUserid(game: Game, userid: Int) {
        var stmt: Statement? = null
        try {
            stmt = conn?.createStatement()
            val query = ("INSERT INTO $TABLE_PENDING ($GAME_NAME, $GAME_PLATFORM, $GAME_STATUS, $GAME_USERID) VALUES ('${game.name}', '${game.platform}', '${game.status}', $userid)")
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

    fun updatePassword(userid: Int, password: String) {
        var stmt: Statement? = null
        try {
            stmt = conn?.createStatement()
            val query = ("UPDATE $TABLE_USER SET $PASSWORD_USER = '$password' WHERE $ID = $userid")
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

}