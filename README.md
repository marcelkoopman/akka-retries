# actorflow
Akka messaging retries

This scala akka project demonstrates using retries on failed futures.
Instead of retrying the actor, using supervision, the message is propagated to the parent as failed work.
The parent can then decide to retry the same message, which is available as original message.
