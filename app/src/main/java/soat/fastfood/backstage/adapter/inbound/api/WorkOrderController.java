package soat.fastfood.backstage.adapter.inbound.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat.fastfood.backstage.adapter.inbound.api.dto.UpdateWorkOrderStatusDto;
import soat.fastfood.backstage.application.usecase.retrieve.get.GetWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.retrieve.get.GetWorkOrderUseCase;
import soat.fastfood.backstage.application.usecase.retrieve.list.ListWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.retrieve.list.ListWorkOrderUseCase;
import soat.fastfood.backstage.application.usecase.update.UpdateWorkOrderCommand;
import soat.fastfood.backstage.application.usecase.update.UpdateWorkOrderUseCase;

@RestController
@RequiredArgsConstructor
@RequestMapping("/work-orders")
public class WorkOrderController {

    private final GetWorkOrderUseCase getWorkOrderUseCase;
    private final ListWorkOrderUseCase listWorkOrderUseCase;
    private final UpdateWorkOrderUseCase updateWorkOrderUseCase;

    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        final var output = this.getWorkOrderUseCase.execute(new GetWorkOrderCommand(id));
        return ResponseEntity.ok(output);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam("status") String status) {
        final var outputs = this.listWorkOrderUseCase.execute(new ListWorkOrderCommand(status));
        return ResponseEntity.ok(outputs);
    }

    @PutMapping("{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody UpdateWorkOrderStatusDto dto) {
        this.updateWorkOrderUseCase.execute(new UpdateWorkOrderCommand(id, dto.status()));
        return ResponseEntity.noContent().build();
    }

}
