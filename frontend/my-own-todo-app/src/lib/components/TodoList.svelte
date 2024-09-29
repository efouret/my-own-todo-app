<!-- src/lib/components/TodoList.svelte -->
<script lang="ts">
	import TodoItem from '$lib/components/TodoItem.svelte'
	import { todos } from '$lib/stores/todos'
	import type { Todo } from '$lib/types'

	// Reactive variables to hold separated todos
	let todosWithDueDates: Todo[] = []
	let todosWithoutDueDates: Todo[] = []

	// Reactive statements to separate and sort todos whenever they change
	$: {
		const allTodos = $todos

		// Separate todos
		todosWithDueDates = allTodos
			.filter(todo => todo.dueDate)
			.sort((a, b) => new Date(a.dueDate!).getTime() - new Date(b.dueDate!).getTime())

		todosWithoutDueDates = allTodos.filter(todo => !todo.dueDate)
	}

	// Function to determine the status of a todo based on its due date
	function getTodoStatus(dueDate: string): 'late' | 'today' | 'future' {
		const today = new Date()
		const due = new Date(dueDate)

		// Remove time portion for accurate comparison
		today.setHours(0, 0, 0, 0)
		due.setHours(0, 0, 0, 0)

		if (due < today) return 'late'
		if (due.getTime() === today.getTime()) return 'today'
		return 'future'
	}
</script>

<div class="todo-container">
	<!-- Column for Todos without Due Dates -->
	<div class="column">
		<h2 class="title is-4">Todos Without Due Dates</h2>
		{#if todosWithoutDueDates.length > 0}
			<ul>
				{#each todosWithoutDueDates as todo (todo.id)}
					<TodoItem {todo} />
				{/each}
			</ul>
		{:else}
			<p class="has-text-grey-light">No todos without due dates.</p>
		{/if}
	</div>

	<!-- Column for Todos with Due Dates -->
	<div class="column">
		<h2 class="title is-4">Todos with Due Dates</h2>
		{#if todosWithDueDates.length > 0}
			<ul>
				{#each todosWithDueDates as todo (todo.id)}
					<TodoItem
						{todo}
						status={getTodoStatus(todo.dueDate ?? '')}
					/>
				{/each}
			</ul>
		{:else}
			<p class="has-text-grey-light">No todos with due dates.</p>
		{/if}
	</div>
</div>

<style>
    .todo-container {
        display: flex;
        gap: 2rem;
        flex-wrap: wrap;
    }

    .column {
        flex: 1;
        min-width: 300px;
    }

    h2.title.is-4 {
        border-bottom: 2px solid #ccc;
        padding-bottom: 0.5rem;
        margin-bottom: 1rem;
    }

    ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    /* Optional: Responsive Design */
    @media (max-width: 768px) {
        .todo-container {
            flex-direction: column;
        }
    }
</style>
