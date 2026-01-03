const payload = $input.first().json;
let eventType = payload.event_type || 'unknown';
let targetWorkflowId = null;

if (eventType === 'new_lead' || (payload.email && !payload.lead_id)) {
    eventType = 'new_lead';
    targetWorkflowId = 'RHj1TAqBazxNFriJ';
} else if (eventType === 'email_opened') {
    eventType = 'email_opened';
}

return {
    json: {
        ...payload,
        event_type: eventType,
        target_workflow_id: targetWorkflowId,
        orchestrator_timestamp: new Date().toISOString(),
        orchestrator_execution_id: $execution.id
    }
};
