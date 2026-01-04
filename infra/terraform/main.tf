# Secret
resource "kubernetes_secret" "backstage-secret" {
  metadata {
    name      = "backstage-secret"
    namespace = "backstage"
  }

  data = {
    AWS_REGION = var.aws_region
    AWS_ACCESS_KEY_ID = var.aws_access_key
    AWS_SECRET_ACCESS_KEY = var.aws_secret_key
  }

  type = "Opaque"
}

resource "kubernetes_manifest" "backstage" {
  manifest = yamldecode(file("${path.module}/../kubernetes/app.yaml"))
}
