variable "project_id" {
  description = "GCP Project ID"
  type        = string
}

variable "gcp_region" {
  description = "Регион GCP"
  type        = string
  default     = "us-central1"
}

variable "env" {
  description = "Окружение"
  type        = string
  default     = "dev"
}
