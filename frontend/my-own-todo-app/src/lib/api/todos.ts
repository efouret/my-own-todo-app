// src/routes/api/todos.ts
import axios from 'axios'
import { todos } from '$lib/stores/todos'
import type { Todo } from '$lib/types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export const fetchTodos = async (): Promise<void> => {
	try {
		const response = await axios.get<Todo[]>(`${API_BASE_URL}/tasks`)
		todos.set(response.data)
	} catch (error) {
		console.error('Error fetching todos:', error)
	}
}

export const addTodo = async (todo: Partial<Todo>): Promise<void> => {
	try {
		const response = await axios.post(`${API_BASE_URL}/tasks`, todo, {
			// Configure axios to get the full response including headers
			validateStatus: (status) => status >= 200 && status < 300,
			// Optionally, set headers if needed
		})

		// Extract the Location header from the response
		const locationHeader = response.headers['location'] || response.headers['Location']
		if (locationHeader) {
			// Fetch the new todo using the Location URL
			const newTodoResponse = await axios.get<Todo>(locationHeader)
			const newTodo = newTodoResponse.data

			// Update the store with the new todo
			todos.update(current => [...current, newTodo])
		} else {
			console.warn('No Location header found in the response.')
			// Optionally, re-fetch all todos
			await fetchTodos()
		}
	} catch (error) {
		console.error('Error adding todo:', error)
		throw error // Re-throw to handle in components
	}
}

export const removeTodo = async (id: string): Promise<void> => {
	try {
		await axios.delete(`${API_BASE_URL}/tasks/${id}`)
		todos.update(current => current.filter(todo => todo.id !== id))
	} catch (error) {
		console.error('Error removing todo:', error)
	}
}

export const toggleTodo = async (
	id: string,
	updates: Partial<Todo>,
): Promise<void> => {
	try {
		const response = await axios.put<Todo>(`${API_BASE_URL}/tasks/${id}/done`, updates)
		todos.update(current =>
			current.map(todo => (todo.id === id ? response.data : todo)),
		)
	} catch (error) {
		console.error('Error toggling todo:', error)
	}
}

export async function updateTodo(id: string, data: Partial<Todo>) {
	const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
		method: 'PUT',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(data),
	})
	if (!response.ok) {
		throw new Error('Failed to update todo')
	}
	return response.json()
}
