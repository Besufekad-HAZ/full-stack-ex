'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import axios from 'axios'
import { CloudArrowUpIcon, CheckCircleIcon } from '@heroicons/react/24/outline'

interface Bank {
  id: number
  value: string
}

interface Branch {
  id: number
  value: string
  bankId: number
}

export default function KYCFormPage() {
  const router = useRouter()
  const [formData, setFormData] = useState({
    bankId: '',
    branchId: '',
    bankName: '',
    branchName: '',
    accountName: '',
    accountNumber: '',
    proofOfBankAccount: null as File | null
  })
  const [banks, setBanks] = useState<Bank[]>([])
  const [branches, setBranches] = useState<Branch[]>([])
  const [errors, setErrors] = useState<{[key: string]: string}>({})
  const [fileError, setFileError] = useState('')

  // Load banks on component mount
  useEffect(() => {
    fetchBanks()

    // Check if user is logged in
    const userSession = sessionStorage.getItem('userSession')
    if (!userSession) {
      router.push('/')
    }
  }, [router])

  // Load branches when bank is selected
  useEffect(() => {
    if (formData.bankId) {
      fetchBranches(parseInt(formData.bankId))
    } else {
      setBranches([])
      setFormData(prev => ({
        ...prev,
        branchId: '',
        branchName: ''
      }))
    }
  }, [formData.bankId])

  const fetchBanks = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/banks')
      setBanks(response.data)
    } catch (error) {
      console.error('Error fetching banks:', error)
    }
  }

  const fetchBranches = async (bankId: number) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/branches?bank_id=${bankId}`)
      setBranches(response.data)
    } catch (error) {
      console.error('Error fetching branches:', error)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }

    if (name === 'bankId') {
      const selectedBank = banks.find(bank => bank.id === parseInt(value))
      setFormData(prev => ({
        ...prev,
        bankId: value,
        bankName: selectedBank?.value || '',
        branchId: '',
        branchName: ''
      }))
    } else if (name === 'branchId') {
      const selectedBranch = branches.find(branch => branch.id === parseInt(value))
      setFormData(prev => ({
        ...prev,
        branchId: value,
        branchName: selectedBranch?.value || ''
      }))
    } else if (name === 'accountNumber') {
      // Only allow numbers
      const numericValue = value.replace(/\D/g, '')
      setFormData(prev => ({
        ...prev,
        [name]: numericValue
      }))
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }))
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    setFileError('')

    if (file) {
      // Validate file type
      const allowedTypes = ['application/pdf', 'image/png', 'image/jpeg', 'image/jpg']
      if (!allowedTypes.includes(file.type)) {
        setFileError('Please upload a PDF, PNG, or JPG file')
        return
      }

      // Validate file size (max 5MB)
      const maxSize = 5 * 1024 * 1024
      if (file.size > maxSize) {
        setFileError('File size must be less than 5MB')
        return
      }

      setFormData(prev => ({
        ...prev,
        proofOfBankAccount: file
      }))
    }
  }

  const validateForm = () => {
    const newErrors: {[key: string]: string} = {}

    if (!formData.bankId) {
      newErrors.bankId = 'Bank selection is required'
    }

    if (!formData.branchId) {
      newErrors.branchId = 'Branch selection is required'
    }

    if (!formData.accountName.trim()) {
      newErrors.accountName = 'Account name is required'
    }

    if (!formData.accountNumber.trim()) {
      newErrors.accountNumber = 'Account number is required'
    } else if (formData.accountNumber.length < 10) {
      newErrors.accountNumber = 'Account number must be at least 10 digits'
    }

    if (!formData.proofOfBankAccount) {
      newErrors.proofOfBankAccount = 'Proof of bank account is required'
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) {
      return
    }

    // Store form data in session storage for confirmation page
    const formDataToStore = {
      ...formData,
      proofOfBankAccount: formData.proofOfBankAccount?.name || ''
    }

    sessionStorage.setItem('kycFormData', JSON.stringify(formDataToStore))

    // Navigate to confirmation page
    router.push('/confirmation')
  }

  const isFormValid = formData.bankId && formData.branchId && formData.accountName.trim() &&
                     formData.accountNumber.trim() && formData.proofOfBankAccount

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-mpesa-green-600 py-3 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center">
            <div className="text-white font-bold text-xl">M-PESA</div>
            <div className="text-white ml-2 text-sm">Safaricom</div>
          </div>
          <div className="flex space-x-4">
            <button className="text-white hover:text-green-200 text-sm font-medium">APPLY</button>
            <button className="text-white hover:text-green-200 text-sm font-medium">RECOMMEND</button>
            <button className="text-white hover:text-green-200 text-sm font-medium">LOGIN</button>
          </div>
        </div>
      </div>

      {/* Navigation Steps */}
      <div className="bg-white border-b border-gray-200 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-8">
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-gray-600">1</span>
                </div>
                <span className="ml-2 text-sm font-medium text-gray-600">Check Merchant</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-gray-600">2</span>
                </div>
                <span className="ml-2 text-sm font-medium text-gray-600">Distribution Detail</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-gray-600">3</span>
                </div>
                <span className="ml-2 text-sm font-medium text-gray-600">Business Type</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-gray-600">4</span>
                </div>
                <span className="ml-2 text-sm font-medium text-gray-600">Business Detail</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
                  <span className="text-sm font-medium text-gray-600">5</span>
                </div>
                <span className="ml-2 text-sm font-medium text-gray-600">Business Owner</span>
              </div>
              <div className="flex items-center">
                <div className="w-8 h-8 bg-mpesa-green-600 rounded-full flex items-center justify-center">
                  <CheckCircleIcon className="w-5 h-5 text-white" />
                </div>
                <span className="ml-2 text-sm font-medium text-mpesa-green-600">Fund Withdraw</span>
              </div>
            </div>
            <div className="text-sm text-gray-500">
              ★ Review
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Fund Withdraw Option</h2>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Bank Selection */}
            <div>
              <label className="flex items-center text-sm font-medium text-gray-700 mb-2">
                <span className="w-4 h-4 bg-mpesa-green-600 rounded-full mr-2"></span>
                Bank
              </label>
              <select
                name="bankId"
                value={formData.bankId}
                onChange={handleInputChange}
                className={`select-mpesa ${errors.bankId ? 'border-red-500' : ''}`}
              >
                <option value="">Select Bank</option>
                {banks.map(bank => (
                  <option key={bank.id} value={bank.id}>{bank.value}</option>
                ))}
              </select>
              {errors.bankId && <p className="error-text">{errors.bankId}</p>}
            </div>

            {/* Branch Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Select Branch
              </label>
              <select
                name="branchId"
                value={formData.branchId}
                onChange={handleInputChange}
                disabled={!formData.bankId}
                className={`select-mpesa ${errors.branchId ? 'border-red-500' : ''} ${!formData.bankId ? 'opacity-50 cursor-not-allowed' : ''}`}
              >
                <option value="">Select Branch</option>
                {branches.map(branch => (
                  <option key={branch.id} value={branch.id}>{branch.value}</option>
                ))}
              </select>
              {errors.branchId && <p className="error-text">{errors.branchId}</p>}
            </div>

            {/* Account Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Account Name
              </label>
              <input
                type="text"
                name="accountName"
                value={formData.accountName}
                onChange={handleInputChange}
                placeholder="Enter Account Name"
                className={`input-mpesa ${errors.accountName ? 'border-red-500' : ''}`}
              />
              {errors.accountName && <p className="error-text">{errors.accountName}</p>}
            </div>

            {/* Account Number */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Account Number
              </label>
              <input
                type="text"
                name="accountNumber"
                value={formData.accountNumber}
                onChange={handleInputChange}
                placeholder="Enter Account Number"
                className={`input-mpesa ${errors.accountNumber ? 'border-red-500' : ''}`}
              />
              {errors.accountNumber && <p className="error-text">{errors.accountNumber}</p>}
            </div>

            {/* Proof of Bank Account */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Proof of Bank Account
              </label>
              <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md">
                <div className="space-y-1 text-center">
                  <CloudArrowUpIcon className="mx-auto h-12 w-12 text-gray-400" />
                  <div className="flex text-sm text-gray-600">
                    <label htmlFor="file-upload" className="relative cursor-pointer bg-white rounded-md font-medium text-mpesa-green-600 hover:text-mpesa-green-500 focus-within:outline-none focus-within:ring-2 focus-within:ring-offset-2 focus-within:ring-mpesa-green-500">
                      <span>Upload a file</span>
                      <input
                        id="file-upload"
                        name="file-upload"
                        type="file"
                        accept=".pdf,.png,.jpg,.jpeg"
                        onChange={handleFileChange}
                        className="sr-only"
                      />
                    </label>
                    <p className="pl-1">or drag and drop</p>
                  </div>
                  <p className="text-xs text-gray-500">PDF, PNG, JPG up to 5MB</p>
                </div>
              </div>
              {formData.proofOfBankAccount && (
                <p className="mt-2 text-sm text-green-600">
                  Selected: {formData.proofOfBankAccount.name}
                </p>
              )}
              {fileError && <p className="error-text">{fileError}</p>}
              {errors.proofOfBankAccount && <p className="error-text">{errors.proofOfBankAccount}</p>}
            </div>

            {/* Action Buttons */}
            <div className="flex justify-end space-x-4 pt-6">
              <button
                type="button"
                onClick={() => router.push('/')}
                className="btn-mpesa-outline px-6 py-2"
              >
                Back
              </button>
              <button
                type="submit"
                disabled={!isFormValid}
                className={`btn-mpesa px-6 py-2 ${!isFormValid ? 'opacity-50 cursor-not-allowed' : ''}`}
              >
                Next
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
