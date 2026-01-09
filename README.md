# Exemplo de mensagem para envio do Pedido ao Backstage

```json
{
	"data": {
		"id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
		"orderNumber": "1",
		"items": [
			{
				"name": "xpto",
				"quantity": 1
			},
			{
				"name": "xpto2",
				"quantity": 2
			}
		]
	}
}
```

# Exemplo da mensagem enviada com as atualizações de status

```json
{
	"data": {
		"id": "a866f7ba-3c24-4d1e-b138-0f80d1300cc2",
		"status": "PREPARING"
	}
}
```

Possiveis status do WorkOrder (Pedido após chegar ao Backstage):

1. RECEIVED (Status inicial ao chegar no Backstage. Não notificado)
2. PREPARING (Status quando o preparo do pedido inicia. Notificado)
3. READY (Status quando o pedido está pronto para entrega. Notificado)
4. DELIVERED (Status quando o pedido foi entregue ao cliente. Notificado)
