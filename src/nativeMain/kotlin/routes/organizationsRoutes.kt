package routes

import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.organizationsRoutes() {
  route("/organizations") {
    // Get Organization Info
    get("/me") {

    }
    route("/users") {
      // Get User
      get("/{userId}") {

      }
      // List Users
      get {

      }
      // Update User
      post("/{userId}") {

      }
      // Remove User
      delete("/{userId}") {

      }
    }
    route("/invites") {
      // Get Invite
      get("/{inviteId}") {

      }
      // List Invites
      get {

      }
      // Create Invite
      post {

      }
      // Delete Invite
      delete("/{inviteId}") {

      }
    }
    route("/workspaces") {
      // List Workspaces
      get {

      }
      // Get Workspace
      get("/{workspaceId}") {

      }
      // Update Workspace
      post("/{workspaceId}") {

      }
      // Create Workspace
      post {

      }
      // Archive Workspace
      post("/{workspaceId}/archive") {

      }
      // Get Workspace Member
      get("/{workspaceId}/members/{userId}") {

      }
      // List Workspace Members
      get("/{workspaceId}/members") {

      }
      // Add Workspace Member
      post("/{workspaceId}/members") {

      }
      // Update Workspace Member
      post("/{workspaceId}/members/{userId}") {

      }
      // Delete Workspace Member
      delete("/{workspaceId}/members/{userId}") {

      }
    }
    route("/api_keys") {
      // Get API Key
      get("/{apiKeyId}") {

      }
      // List API Keys
      get {

      }
      // Update API Keys
      post("/{apiKeyId}") {

      }
    }
    // Get Usage Report for the Messages API
    get("/usage_report/messages") {

    }
    // Get Cost Report
    get("/cost_report") {

    }
    // Get Claude Code Usage Report
    get("/usage_report/claude_code") {

    }
  }
}