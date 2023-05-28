rootProject.name = "HW4"

include("framework/auth")
include("framework/core")
include("services/auth")
include("services/rent")
include("gateway")

project(":framework/auth").name = "framework_auth"
project(":framework/core").name = "framework_core"
project(":services/auth").name = "services_auth"
project(":services/rent").name = "services_rent"
project(":gateway").name = "gateway"