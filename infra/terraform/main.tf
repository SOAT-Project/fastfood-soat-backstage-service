# Aplica todo o manifesto YAML com múltiplos documentos
resource "kubectl_manifest" "backstage" {
  yaml_body = file("${path.module}/../kubernetes/app.yaml")

  # Aguarda todos os recursos serem criados
  wait = true

  # Aguarda o rollout do deployment
  wait_for_rollout = true

  # Sobrescreve os recursos se já existirem
  force_conflicts = true

  # Aplica mudanças no servidor
  server_side_apply = true
}

# Secret com credenciais AWS (criado após o namespace)
resource "kubernetes_secret" "backstage_secret" {
  metadata {
    name      = "backstage-secret"
    namespace = "backstage"
  }

  data = {
    AWS_REGION            = var.aws_region
    AWS_ACCESS_KEY_ID     = var.aws_access_key
    AWS_SECRET_ACCESS_KEY = var.aws_secret_key
  }

  type = "Opaque"

  # Garante que o namespace existe antes de criar o secret
  depends_on = [kubectl_manifest.backstage]
}
