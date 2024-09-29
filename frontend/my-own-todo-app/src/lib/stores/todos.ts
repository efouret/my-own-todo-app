// src/lib/stores/todos.ts
import { writable } from 'svelte/store'
import type { Todo } from '../types'
import { fetchTodos as fetch } from '$lib/api/todos'

const todos = writable<Todo[]>([])

const fetchTodos = async () => {
	await fetch()
}

export { todos, fetchTodos }
